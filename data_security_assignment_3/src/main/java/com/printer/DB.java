package com.printer;

import java.sql.*;

import org.apache.commons.codec.digest.DigestUtils;

public class DB implements IDB {

    private static final String dbUrl = "jdbc:sqlite:C:/sqlite/db/printerDB.db";

    // private static final String dbUrl = "jdbc:sqlite:/Users/lkj/Downloads/sqlite-tools-osx-x86-3360000/db/printerDB.db";
    // private static final String dbUrl = "jdbc:sqlite:/home/lkj/sqlite-tools-linux-x86-3360000/sqlite-tools-linux-x86-3360000/db/printerDB.db";
    
    public DB() {
        createNewDatabase();

        createPasswordsTable();

        addPasswordToDb("Bob", "pass_1");
        addPasswordToDb("George", "pass_4");
        addPasswordToDb("Cecilia", "pass_2");
        addPasswordToDb("David", "pass_3");
        addPasswordToDb("Henry", "pass_5");
    }

    @Override
    public boolean authenticateUser(String pass, String username) {

        String sql = "SELECT password FROM passwords WHERE password=? AND username=?";

        try (Connection conn = connect()) {
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1, pass);
            psmt.setString(2, username);

            ResultSet rs = psmt.executeQuery();

            conn.close();
            return rs.next();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void addPasswordToDb(String username, String password) {
        String sql = "INSERT INTO passwords(username,password) VALUES(?,?)";
        String pass_sha256hex = DigestUtils.sha256Hex(password);

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, pass_sha256hex);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Override
    public void createNewDatabase() {

        try (Connection conn = this.connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                conn.close();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void createPasswordsTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS passwords (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	username text NOT NULL,\n"
                + "	password text NOT NULL\n"
                + ");";

        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
