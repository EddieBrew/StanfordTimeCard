package com.StanfordTimeCard.StanfordTimeCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.table.DefaultTableModel;

/**
 * An editable table model backed by a single DB table. addded
 *
 * <pre>
 * Column layout rule:
 *   model column 0        = primary key (usually "id"), hidden in the view
 *   model columns 1..n    = editable fields, mapped to DB column names
 * </pre>
 */
public class SQLTableModel extends DefaultTableModel {

    private final DatabaseConnection db;
    private final String table; // e.g. "employees"
    private final String pkColumn; // e.g. "id"
    private final String[] dbColumns; // parallel to model cols 1..n (excludes pk)
    private final Class<?>[] columnTypes; // parallel to model cols 0..n (pk first)
    private final String[] displayNames; // header labels, pk first

    private final Map<Integer, Object[]> snapshots = new HashMap<>();
    private final List<Object> pendingDeletes = new ArrayList<>();
    private boolean dirty = false;
    private Runnable dirtyListener = () -> {
    };

    /**
     * @param db shared connection
     * @param table DB table name
     * @param pkColumn primary key column name (e.g. "id")
     * @param dbColumns editable DB columns in display order (excludes pk)
     * @param displayNames header labels, one per dbColumn, preceded by the pk
     * label
     * @param columnTypes Java types, one per model column (pk first)
     */
    public SQLTableModel(DatabaseConnection db, String table, String pkColumn,
            String[] dbColumns, String[] displayNames,
            Class<?>[] columnTypes) {
        super(new Object[][]{},
                prepend(pkColumn, displayNames));
        this.db = db;
        this.table = table;
        this.pkColumn = pkColumn;
        this.dbColumns = dbColumns;
        this.displayNames = displayNames;
        this.columnTypes = columnTypes;
    }

    private static Object[] prepend(String head, String[] rest) {
        Object[] out = new Object[rest.length + 1];
        out[0] = head;
        System.arraycopy(rest, 0, out, 1, rest.length);
        return out;
    }

    // ---- model identity ----
    @Override
    public boolean isCellEditable(int r, int c) {
        return c != 0;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return columnTypes[c];
    }

    // ---- dirty tracking ----
    public void setDirtyListener(Runnable r) {
        this.dirtyListener = r;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        super.setValueAt(value, row, col);
        markDirty();
    }

    private void markDirty() {
        if (!dirty) {
            dirty = true;
            dirtyListener.run();
        }
    }

    // =========================================================
    // LOAD
    // =========================================================
    /**
     * Load rows from an arbitrary SELECT. First column MUST be the pk.
     */
    public void load(PreparedStatement ps) throws SQLException {
        setRowCount(0);
        snapshots.clear();
        pendingDeletes.clear();
        //System.out.println("The number of rows are 0");

        try (ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Object[] row = new Object[md.getColumnCount()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                int r = getRowCount();
                addRow(row);
                snapshots.put(r, row.clone());
            }
        }
        dirty = false;
        dirtyListener.run();
    }

    // =========================================================
    // STAGE CHANGES
    // =========================================================
    
    
    /**
     * Adds a blank row to the table.
     */
    public void addBlankRow() {
        Object[] blank = new Object[dbColumns.length + 1];
        blank[0] = null; // pk unknown
        for (int i = 1; i < blank.length; i++) {
            blank[i] = null;
        }
        addRow(blank);
        markDirty();
    }

    /**
     * Queue a row for deletion and remove it from the view.
     */
    public void deleteRowAt(int modelRow) {
        Object pk = getValueAt(modelRow, 0);
        if (pk != null) {
            pendingDeletes.add(pk);
        }
        removeRow(modelRow);
        rebuildSnapshotIndices();
        markDirty();
    }

    /**
     * Snapshots are index-keyed; after a remove, re-key them to the new
     * positions.
     */
    private void rebuildSnapshotIndices() {
        Map<Integer, Object[]> fresh = new HashMap<>();
        for (int r = 0; r < getRowCount(); r++) {
            fresh.put(r, currentRow(r));
        }
        snapshots.clear();
        snapshots.putAll(fresh);
    }

    // =========================================================
    // SAVE
    // =========================================================
   
   
    /**
     * Saves all staged changes to the database.
     *
     * @return the result of the save operation
     * @throws SQLException if a database error occurs
     */
    public SaveResult saveChanges() throws SQLException {
        int updates = 0, inserts = 0, deletes = 0;
        Connection conn = db.get();
        try {
            conn.setAutoCommit(false);

            // UPDATE
            for (int r = 0; r < getRowCount(); r++) {
                Object[] orig = snapshots.get(r);
                if (orig == null || orig[0] == null) {
                    continue;
                }
                if (!rowEquals(orig, currentRow(r))) {
                    updateRow(conn, orig[0], currentRow(r));
                    updates++;
                }
            }

            // INSERT (pk == null AND row has some content)
            for (int r = 0; r < getRowCount(); r++) {
                if (getValueAt(r, 0) == null && rowHasContent(r)) {
                    int newPk = insertRow(conn, currentRow(r));
                    setValueAt(newPk, r, 0);
                    inserts++;
                }
            }

            // DELETE
            for (Object pk : pendingDeletes) {
                deleteRow(conn, pk);
                deletes++;
            }
            pendingDeletes.clear();

            conn.commit();
            dirty = false;
            rebuildSnapshotIndices();
            dirtyListener.run();
            return new SaveResult(updates, inserts, deletes);

        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    
    
    /**
     * Updates a row in the database.
     *
     * @param conn the database connection
     * @param pk the primary key of the row to update
     * @param row the row data
     * @throws SQLException if a database error occurs
     */
    private void updateRow(Connection conn, Object pk, Object[] row) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
        for (int i = 0; i < dbColumns.length; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(dbColumns[i]).append(" = ?");
        }
        sql.append(" WHERE ").append(pkColumn).append(" = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < dbColumns.length; i++) {
                ps.setObject(i + 1, row[i + 1]);
            }
            ps.setObject(dbColumns.length + 1, pk);
            ps.executeUpdate();
        }
    }

    
    /**
     * Inserts a row into the database.
     *
     * @param conn the database connection
     * @param row the row data
     * @return the generated primary key
     * @throws SQLException if a database error occurs
     */
    private int insertRow(Connection conn, Object[] row) throws SQLException {
        StringBuilder cols = new StringBuilder(), qs = new StringBuilder();
        for (int i = 0; i < dbColumns.length; i++) {
            if (i > 0) {
                cols.append(", ");
                qs.append(", ");
            }
            cols.append(dbColumns[i]);
            qs.append("?");
        }
        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + qs + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < dbColumns.length; i++) {
                ps.setObject(i + 1, row[i + 1]);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    
    /**
     * Deletes a row from the database.
     *
     * @param conn the database connection
     * @param pk the primary key of the row to delete
     * @throws SQLException if a database error occurs
     */
    private void deleteRow(Connection conn, Object pk) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM " + table + " WHERE " + pkColumn + " = ?")) {
            ps.setObject(1, pk);
            ps.executeUpdate();
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    
    /**
     * Returns the current row as an array.
     *
     * @param r the row index
     * @return the row data
     */
    private Object[] currentRow(int r) {
        Object[] v = new Object[getColumnCount()];
        for (int c = 0; c < v.length; c++) {
            v[c] = getValueAt(r, c);
        }
        return v;
    }

    
    /**
     * Compares two rows, ignoring the primary key.
     *
     * @param a the first row
     * @param b the second row
     * @return true if the rows are equal, false otherwise
     */
    private boolean rowEquals(Object[] a, Object[] b) {
        for (int i = 1; i < a.length; i++) // skip pk
        {
            if (!Objects.equals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * Checks whether a row contains any non-blank data.
     *
     * @param r the row index
     * @return true if the row has content, false otherwise
     */
    private boolean rowHasContent(int r) {
        for (int c = 1; c < getColumnCount(); c++) {
            Object v = getValueAt(r, c);
            if (v != null && !v.toString().isBlank()) {
                return true;
            }
        }
        return false;
    }

    // =========================================================
    // RESULT
    // =========================================================

    
    /**
     * Represents the result of a save operation.
     *
     * @param updates the number of updated rows
     * @param inserts the number of inserted rows
     * @param deletes the number of deleted rows
     */
    public record SaveResult(int updates, int inserts, int deletes) {

        @Override
        public String toString() {
            return String.format("Saved. %d updated, %d inserted, %d deleted.",
                    updates, inserts, deletes);
        }
    }
}
