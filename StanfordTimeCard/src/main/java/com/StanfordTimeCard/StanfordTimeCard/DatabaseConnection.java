package com.StanfordTimeCard.StanfordTimeCard

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DatabaseConnection {

    private final String url, user, pass;
    private Connection conn;

    public DatabaseConnection(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * Lazy connect; returns the same Connection for the lifetime of the app.
     */
    public Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(true);
        }
        return conn;
    }

    /**
     * Show a dialog and exit if the DB can't be reached at startup.
     */
    public void testOrExit(java.awt.Component parent) {
        try {
            get();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parent,
                    "DB connection failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
