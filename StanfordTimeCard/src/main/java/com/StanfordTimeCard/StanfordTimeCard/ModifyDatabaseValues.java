package com.StanfordTimeCard.StanfordTimeCard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * ModifyDatabaseValues
 */
public class ModifyDatabaseValues extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASS;
    private static MySQLConnect mySQLDatabase;
    private static final String inputFile = "dailyhours.csv";
    private final static String filename = "mysqlsignonstuff.txt";

    Connection conn;

    private final String TIMECARD_DATABASE = "dailyhours";

    // -------- Model column indices --------
    private static final int RECORD_ID = 0; // hidden
    private static final int DATE = 1;
    private static final int DAY = 2;
    private static final int EVENTNUMBER = 3;
    private static final int EVENTNAME = 4;
    private static final int TIME = 5;
    private static final int REGHOURS = 6;
    private static final int OTHOURS = 7;

    private JTable table;
    private final DefaultTableModel model;
    // private MySQLConnect myDatabase;

    // Column index that corresponds to the primary key
    private static final int PRIMARY_KEY_COL = 0; // "id"

    // private TableRowSorter<DefaultTableModel> sorter;
    public ModifyDatabaseValues(String url, String id, String pw) {
        setTitle("My Query Table");
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        this.DB_URL = url;
        this.DB_USER = id;
        this.DB_PASS = pw;

        connectToDB(); // -------- Model --------
        createButtonPanel();

        model = new DefaultTableModel(
                new Object[]{"RECORD_ID", "DATE", "DAY", "EVENTNUMBER", "EVENTNAME", "TIME", "REGHOURS", "OTHOURS"}, 0) {

            /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                // ID and hire_date are read-only in this example
                return column != PRIMARY_KEY_COL; // disallow editing id.
            }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.putClientProperty("terminateEditOnFocusLost", true); // commit on focus loss

        // Listen for cell edits and push to DB
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && col >= 0) {
                    updateDatabase(row, col);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadData();

    }

    // =====================================================================
    // DB CONNECTION
    // =====================================================================
    private void connectToDB() {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB connection failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // =====================================================================
    // LOAD
    // =====================================================================
    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT RECORD_ID, DATE, DAY, EVENTNUMBER, EVENTNAME, TIME, REGHOURS, OTHOURS "
                + "FROM " + TIMECARD_DATABASE + " ORDER BY DATE DESC";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("RECORD_ID"));
                row.add(rs.getString("DATE"));
                row.add(rs.getString("DAY"));
                row.add(rs.getInt("EVENTNUMBER"));
                row.add(rs.getString("EVENTNAME"));
                row.add(rs.getString("TIME"));
                row.add(rs.getFloat("REGHOURS"));
                row.add(rs.getFloat("OTHOURS"));
                model.addRow(row);
            }

            // new Object[]{"RECORD_ID", "DATE", "AREA", "ITEMS", "COST", "FILENAME",
            // "INFO", "VALUE"}
        } catch (SQLException ex) {
            showError("Load failed", ex);
        }
    }

    // =====================================================================
    // UPDATE (by hidden id)
    // =====================================================================
    private void updateDatabase(int row, int col) {
        String columnName = model.getColumnName(col);
        Object newValue = model.getValueAt(row, col);
        Object pkValue = model.getValueAt(row, PRIMARY_KEY_COL);

        String sql = "UPDATE " + TIMECARD_DATABASE + " SET " + columnName + " = ? WHERE RECORD_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, newValue);
            ps.setObject(2, pkValue);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.out.println("No row updated for id " + pkValue);
            }
        } catch (SQLException ex) {
            showError("Update failed", ex);
            loadData(); // reload to roll back the visual change
        }
    }

    // =====================================================================
    // BUTTONS
    // =====================================================================
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addBtn = new JButton("Add Row");
        JButton deleteBtn = new JButton("Delete Row");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> addRow());
        deleteBtn.addActionListener(e -> deleteRow());
        refreshBtn.addActionListener(e -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            loadData();
        });

        panel.add(addBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        return panel;
    }

    // =====================================================================
    // ADD
    // =====================================================================
    private void addRow() {
        String DATE = JOptionPane.showInputDialog(this, "DATE:");
        if (DATE == null || DATE.isBlank()) {
            return;
        }
        String DAY = JOptionPane.showInputDialog(this, "DAY:");
        String EVENTNUMBER = JOptionPane.showInputDialog(this, "EVENTNUMBER:");
        String EVENTNAME = JOptionPane.showInputDialog(this, "EVENTNAME:");
        String TIME = JOptionPane.showInputDialog(this, "TIME:");
        String REGHOURS = JOptionPane.showInputDialog(this, "REGHOURS:");
        String OTHOURS = JOptionPane.showInputDialog(this, "OTHOURS:");




        

        String sql = "INSERT INTO " + TIMECARD_DATABASE
                + " (DATE, DAY, EVENTNUMBER, EVENTNAME, TIME , REGHOURS , OTHOURS) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, DATE);
            ps.setString(2, DAY);
            ps.setInt(3, Integer.parseInt(EVENTNUMBER));
            ps.setString(4, EVENTNAME);
            ps.setString(5, TIME);
            ps.setFloat(6, Float.parseFloat(REGHOURS));
            ps.setFloat(7, Float.parseFloat(OTHOURS));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    model.addRow(new Object[]{newId, DATE, DAY, EVENTNUMBER, EVENTNAME, TIME, REGHOURS, OTHOURS});
                }
            }
        } catch (SQLException ex) {
            showError("Insert failed", ex);
        }
    }

    // =====================================================================
    // DELETE (must convert view row → model row!)
    // =====================================================================
    private void deleteRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        // Convert: table's selected row is a VIEW index because of the sorter
        int modelRow = table.convertRowIndexToModel(viewRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected row?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Object pkValue = model.getValueAt(modelRow, RECORD_ID); // hidden id

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM " + TIMECARD_DATABASE + " WHERE RECORD_ID = ?")) {
            ps.setObject(1, pkValue);
            ps.executeUpdate();
            model.removeRow(modelRow); // use MODEL index
        } catch (SQLException ex) {
            showError("Delete failed", ex);
        }
    }

    // =====================================================================
    // HELPERS
    // =====================================================================
    

  /**
   * @param title
   * @param ex
   */
    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, title + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {

        final String DB_URL = "jdbc:mysql://127.0.0.1:3306/home_improvement?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        final String DB_USER = "rbrewer";
        final String DB_PASS = "Great2BeAliveN2022#";

        try {
            SwingUtilities.invokeLater(() -> new ModifyDatabaseValues(DB_URL, DB_USER, DB_PASS).setVisible(true));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //
    }

}
