package com.homeimprovement;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

/**
 * Builds a WHERE clause from search widgets. Copy this pattern per app; only
 * the fields and the getWhere() body change.
 */
public class QueryPanel extends JPanel {

    //private final JLabel totalLabel = new JLabel("Total");
    private boolean suppressSearch = false;
    private final JTextField costField = new JTextField(12);
    private final JComboBox<String> areaFilter = new JComboBox<>(
            new String[]{"", "ATTIC", "BACKYARD", "BATH1", "BATH2", "BDRM1", "BDRM2", "BDRM3", "BDRM4",
                "FRONTYARD", "GARAGE", "HALLWAY", "KITCHEN", "LIVING ROOM", "ROOF"});
    private final JComboBox<String> itemFilter = new JComboBox<>(
            new String[]{"", "ANTENNA", "CHIMNEY", "CLOSET", "DISHWASHER", "DOOR", "DRYER", "FAN", "FENCE",
                "FLOOR", "FRIG", "GUTTERS", "INTERNET", "LIGHTS", "MIRROR", "MISC", "OUTLETS", "OVEN", "PLANTS",
                "SEWAGE LINE", "SHOWER/TUB", "SINK", "TILE", "TOILET", "TV", "WALLS", "WASHER", "WINDOWS"});
    private final JDateChooser dateFrom = new JDateChooser();
    private final JDateChooser dateTo = new JDateChooser();

    private Runnable searchAction = () -> {
    };
    private Runnable breakdownAction;

    public QueryPanel() {
        super(new FlowLayout(FlowLayout.LEFT));

        dateFrom.setDateFormatString("yyyy-MM-dd");
        dateTo.setDateFormatString("yyyy-MM-dd");
        dateFrom.setPreferredSize(new Dimension(120, 26));
        dateTo.setPreferredSize(new Dimension(120, 26));
        dateFrom.setDate(null);
        dateTo.setDate(null);

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchBtn.addActionListener(e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });
        costField.addActionListener(e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });
        areaFilter.addActionListener(e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });
        itemFilter.addActionListener(e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });
        dateFrom.addPropertyChangeListener("date", e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });
        dateTo.addPropertyChangeListener("date", e -> {
            if (!suppressSearch) {
                searchAction.run();
            }
        });

        clearBtn.addActionListener(e -> {
            suppressSearch = true;

            try {
                costField.setText("");
                areaFilter.setSelectedIndex(0);
                itemFilter.setSelectedIndex(0);
                dateFrom.setDate(null);
                dateTo.setDate(null);
            } finally {
                suppressSearch = false;
            }
            searchAction.run();
        });

        //add(new JLabel("Keyword:")); add(costField);
/* 
       
        // Add a Runnable for the click action
        totalLabel.setForeground(Color.BLUE.darker());
        totalLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        totalLabel.setToolTipText("Click to view breakdown by Room");
        totalLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (breakdownAction != null) {
                    breakdownAction.run();
                }
            }
        });
         */
        //add(totalLabel);
        add(new JLabel("From:"));
        add(dateFrom);
        add(new JLabel("To:"));
        add(dateTo);
        add(new JLabel("Area:"));
        add(areaFilter);
        add(new JLabel("Item:"));
        add(itemFilter);
        add(new JLabel("Cost"));
        add(costField);
        add(searchBtn);
        add(clearBtn);
    }

    /**
     * Sets the action to perform when the search button is clicked.
     *
     * @param action the action to perform
     */
    public void onSearch(Runnable action) {
        this.searchAction = action;
    }

    
    /**
     * Sets the action to perform when the total label is clicked.
     *
     * @param action the action to perform
     */
    public void onTotalClick(Runnable action) {
        this.breakdownAction = action;
    }

    
    
    /**
     * Builds the WHERE clause fragment and parameters.
     *
     * @return the WHERE clause
     */
    public WhereClause buildWhere() {
        StringBuilder w = new StringBuilder();
        List<Object> params = new ArrayList<>();

        String kw = costField.getText().trim();
        if (!kw.isEmpty()) {
            w.append(" AND CAST(COST AS CHAR) LIKE  ? ");
            params.add("%" + kw + "%");
        }

        String area = (String) areaFilter.getSelectedItem();
        if (area != null && !area.isBlank()) {
            w.append(" AND AREA = ? ");
            params.add(area);
        }

        String item = (String) itemFilter.getSelectedItem();
        if (item != null && !item.isBlank()) {
            w.append(" AND ITEMS = ? ");
            params.add(item);
        }

        final String dateCol = "`DATE`";
        java.util.Date from = dateFrom.getDate();
        java.util.Date to = dateTo.getDate();

        // Convert to yyyy-MM-dd text strings
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String fromStr = (from == null) ? null : sdf.format(from);
        String toStr = (to == null) ? null : sdf.format(to);
        if (fromStr != null && toStr != null) {
            w.append(" AND " + dateCol + " BETWEEN ? AND ? ");
            params.add(fromStr);
            params.add(toStr);
        } else if (fromStr != null) {
            w.append(" AND " + dateCol + " >= ? ");
            params.add(fromStr);
        } else if (toStr != null) {
            w.append(" AND " + dateCol + " <= ? ");
            params.add(toStr);
        }

        return new WhereClause(w.toString(), params);
    }

    public record WhereClause(String fragment, List<Object> params) {

    }
}
