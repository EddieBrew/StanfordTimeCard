package com.StanfordTimeCard.StanfordTimeCard;


/*
 * This class represents the main application window for querying and filtering home improvement and repair expenses.
 * It provides a summary table by room and a detailed table for viewing and editing individual expense records. The class requires the QueryPanel class.
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

//Formally RepairsApp class
public class QueryFilterTable extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String URL;
    private final String USER;
    private final String PASS;
    private final String TIMECARD_DATABASE = "dailyhours";// database that stores past locker
    // information
    private final JTable summaryTable;
    private final DefaultTableModel summaryModel;
    private final DatabaseConnection db;
    private final QueryPanel queryPanel = new QueryPanel();
    private final JLabel status = new JLabel(" ");

    private final JTable table;
    private SQLTableModel model;
    private JButton saveBtn;

    public QueryFilterTable(String url, String user, String pass) {
        setTitle("Home Improvements and Repairs Query Panel");
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
        db = new DatabaseConnection(URL, USER, PASS);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 520);
        setLocationRelativeTo(null);
        setVisible(true);

        db.testOrExit(null);

        // Build the summary table
        summaryModel = new DefaultTableModel(new String[]{"REGHOURS","OTHOURS",  "TOTAL HOURS"}, 0) {
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        summaryTable = new JTable(summaryModel);
        summaryTable.setRowHeight(22);
        summaryTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        summaryTable.getColumnModel().getColumn(1).setPreferredWidth(80);

        // Add this sorter!
        TableRowSorter<DefaultTableModel> summarySorter = new TableRowSorter<>(summaryModel);
        summaryTable.setRowSorter(summarySorter);
        setupSummaryTableRenderer();

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.add(new JLabel("  Summary by Hours"), BorderLayout.NORTH);
        summaryPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        summaryPanel.setPreferredSize(new Dimension(300, 0)); // fixed width

        model = new SQLTableModel(
                db,
                "houseexpenses", // table
                "RECORD_ID", // pk
                new String[]{"DATE", "DAY", "EVENTNUMBER", "EVENTNAME", "TIME", "REGHOURS", "OTHOURS"},
                new String[]{"DATE", "DAY", "EVENTNUMBER", "EVENTNAME", "TIME", "REGHOURS", "OTHOURS"},
                new Class<?>[]{
                    Integer.class, // RECORD_ID
                    String.class, // DATE
                    String.class, // DAY
                    Integer.class, // EVENTNUMBER
                    String.class, // EVENTNAME
                    String.class, // TIME
                    Double.class, // REGHOURS
                    Double.class // OTHOURS
                });

        table = new JTable(model);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.putClientProperty("terminateEditOnFocusLost", true);
        TableRowSorter<SQLTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // hide pk
        TableColumn idCol = table.getColumnModel().getColumn(0);
        idCol.setMinWidth(0);
        idCol.setMaxWidth(0);
        idCol.setPreferredWidth(0);

        // dirty → enable Save
        model.setDirtyListener(() -> {
            saveBtn.setEnabled(model.isDirty());
            updateStatus();
        });

        // search triggers reload
        queryPanel.onSearch(this::reload);
        queryPanel.onTotalClick(this::showBreakdown);

        add(queryPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(status, BorderLayout.PAGE_END);
        add(summaryPanel, BorderLayout.EAST);

        reload();
    }

    private void reload() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        try {
            QueryPanel.WhereClause wc = queryPanel.buildWhere();

            String sql = "SELECT RECORD_ID, `DATE`, DAY, EVENTNUMBER, EVENTNAME, TIME, REGHOURS, OTHOURS "
                    + "FROM " + TIMECARD_DATABASE + " WHERE 1=1 " + wc.fragment() + " ORDER BY 'DATE' DESC";

            //System.out.println("SQL: " + sql);
            //System.out.println("params   = " + wc.params());
            try (PreparedStatement ps = db.get().prepareStatement(sql)) {
                List<Object> params = wc.params();
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                model.load(ps);
            }
            status.setText(" Loaded " + model.getRowCount() + " rows.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Invalid filter", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showError("Query100 failed", ex);
        }
        // status.setText(" Loaded " + model.getRowCount() + " rows.");
        updateStatus();

        updateSummary(); // <--- ADD THIS LINE
    }

    private void save() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        try {
            SQLTableModel.SaveResult r = model.saveChanges();
            status.setText(" " + r);
        } catch (SQLException ex) {
            showError("Save failed (rolled back)", ex);
        }
    }

    private JPanel createButtonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Add Row");
        JButton deleteBtn = new JButton("Delete Row");
        saveBtn = new JButton("Save");
        JButton refreshBtn = new JButton("Refresh (discard)");

        saveBtn.setEnabled(false);

        addBtn.addActionListener(e -> {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            model.addBlankRow();
        });

        deleteBtn.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                status.setText(" Select a row to delete.");
                return;
            }
            model.deleteRowAt(table.convertRowIndexToModel(viewRow));
        });

        saveBtn.addActionListener(e -> save());

        refreshBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Discard unsaved changes and reload?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                reload();
            }
        });

        p.add(addBtn);
        p.add(deleteBtn);
        p.add(saveBtn);
        p.add(refreshBtn);
        return p;
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, title + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        //System.out.println(ex.toString());
    }

    private void showBreakdown() {
        String sql = "SELECT DAY, COUNT(*) as REGHOURS, SUM(REGHOURS) as total "
                + "FROM dailyhours "
                + "WHERE total IS NOT NULL "
                + "GROUP BY REGHOURS ORDER BY total DESC";

        StringBuilder sb = new StringBuilder("Cost Breakdown by Room:\n\n");

        try (Statement s = db.get().createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                String regHours = rs.getString("REGHOURS");
                if (regHours == null || regHours.isBlank()) {
                    regHours = "Unassigned";
                }
                int count = rs.getInt("REGHOURS");
                double sum = rs.getDouble("total");
                sb.append(String.format("%-15s  %3d items  %,.2f\n", regHours, count, sum));
            }
        } catch (SQLException ex) {
            showError("Breakdown failed", ex);
            return;
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Cost Breakdown", JOptionPane.INFORMATION_MESSAGE);

    }
    
   
    private void updateSummary() {
        summaryModel.setRowCount(0);   // ← clear rows only, don't touch columns

        QueryPanel.WhereClause wc = queryPanel.buildWhere();
        String sql = "SELECT `DATE`, SUM(REGHOURS) as total_reg, SUM(OTHOURS) as total_ot " +
                     "FROM "+ TIMECARD_DATABASE +   " WHERE 1=1 " + 
                     wc.fragment() + " GROUP BY `DATE` ORDER BY `DATE` DESC";

        double grandReg = 0.0;
        double grandOT = 0.0;
        double grandTotal = 0.0;
        //System.out.println("Summary SQL: " + sql);
        //System.out.println("Summary Params: " + wc.params());
        try (PreparedStatement ps = db.get().prepareStatement(sql)) {
            List<Object> params = wc.params();
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.math.BigDecimal reg = rs.getBigDecimal("total_reg");
                    java.math.BigDecimal ot  = rs.getBigDecimal("total_ot");
                    if (reg == null) reg = java.math.BigDecimal.ZERO;
                    if (ot  == null) ot  = java.math.BigDecimal.ZERO;
                    java.math.BigDecimal dailyTotal = reg.add(ot);

                    summaryModel.addRow(new Object[]{ reg, ot, dailyTotal });

                    grandReg   += reg.doubleValue();
                    grandOT    += ot.doubleValue();
                    grandTotal += dailyTotal.doubleValue();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (summaryModel.getRowCount() > 0) {
            summaryModel.addRow(new Object[]{
                new java.math.BigDecimal(grandReg),
                new java.math.BigDecimal(grandOT),
                new java.math.BigDecimal(grandTotal)
            });
        }
    }
    

    private double getRegHoursTotal() {
        double total = 0.0;
        int regCol = 6; // 0=pk, 1=DATE, 2=DAY........ 6=REGHours
        for (int r = 0; r < model.getRowCount(); r++) {
            Object v = model.getValueAt(r, regCol);
            if (v instanceof BigDecimal) {
                total += ((BigDecimal) v).doubleValue();
            }
        }
        return total;
    }

    private void updateStatus() {
        double total = getRegHoursTotal();
        status.setText(String.format(" Loaded %d rows  |  Total RegHours: %,.2f",
                model.getRowCount(), total));

        // If you added the label to QueryPanel:
        // queryPanel.setTotal(total);
        // Update the side panel
        updateSummary();
    }

    private void setupSummaryTableRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                // Format numbers to 2 decimal places
                if (value instanceof Number) {
                    double amount = ((Number) value).doubleValue();
                    setText(String.format("%.2f", amount)); 
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setText(value == null ? "" : value.toString());
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                // Bold the very last row (the grand total)
                if (row == table.getRowCount() - 1) {
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
                
                return c;
            }
        };
        
        // Apply to all THREE columns (0, 1, and 2)
        summaryTable.getColumnModel().getColumn(0).setCellRenderer(renderer);
        summaryTable.getColumnModel().getColumn(1).setCellRenderer(renderer);
        summaryTable.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    public static void main(String[] args) {
        /* 
        final String URL = "";
        final String USER = "";
        final String PASS = "";

        SwingUtilities.invokeLater(() -> new QueryFilterTable(URL, USER, PASS).setVisible(true));
         */
    }
}
