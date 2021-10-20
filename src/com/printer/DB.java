package com.printer;

import java.sql.*;

public class DB {

    private static final String passUrl = "jdbc:sqlite:C:/sqlite/db/passwords.db";
    private static final String cookieUrl = "jdbc:sqlite:C:/sqlite/db/cookies.db";

    public DB() {
        createNewDatabase(passUrl);
        createNewDatabase(cookieUrl);

        createCookieTable(cookieUrl);
        createPasswordsTable(passUrl);
    }

    void addPasswordToDb(String username, String password) {

    }

    private static Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createNewDatabase(String url) {

        try (Connection conn = connect(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void createPasswordsTable(String url) {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS passwords (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	username text NOT NULL,\n"
                + "	password text NOT NULL\n"
                + ");";

        try (Connection conn = connect(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void createCookieTable(String url) {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS cookies (\n"
                + "	cookieId integer PRIMARY KEY,\n"
                + "	timestamp double NOT NULL\n"
                + ");";

        try (Connection conn = connect(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
