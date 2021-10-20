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

        addPasswordToDb(passUrl, "user1", "hello");
        addPasswordToDb(passUrl, "user2", "hello");
    }

    boolean authenticateUser(String pass, String username){

        String sql = "SELECT password FROM passwords WHERE password=? AND username=?";

        try(Connection conn = connect(passUrl)){

        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,pass);
        psmt.setString(2,username);

        ResultSet rs = psmt.executeQuery();

        return rs.next();

        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    void addPasswordToDb(String url, String username, String password) {
        String sql = "INSERT INTO passwords(username,password) VALUES(?,?)";

        try (Connection conn = connect(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
                + "	cookieId String PRIMARY KEY,\n"
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
