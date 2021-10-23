package com.printer;

import com.sun.jdi.request.StepRequest;

import java.sql.*;

public class DB implements IDB {

    private static final String dbUrl = "jdbc:sqlite:C:/sqlite/db/printerDB.db";
    // private static final String dbUrl = "jdbc:sqlite:/home/lkj/sqlite-tools-linux-x86-3360000/sqlite-tools-linux-x86-3360000/db/printerDB.db";

    public DB() {
        createNewDatabase();

        createCookieTable();
        createPasswordsTable();

        addPasswordToDb("user1", "hello");
        addPasswordToDb("user2", "hello");
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
    public boolean authenticateCookie(Cookie c) {

        String sql = "SELECT cookieId FROM cookies WHERE cookieId=?";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getId());
            pstmt.executeQuery();

            ResultSet rs = pstmt.executeQuery();
            String cId = rs.getString("cookieId");
            // System.out.println("CookieId " + c.getId()+"\n"+"cookie id from query" + cId);

            if (c.getId().equals(cId)) {
                conn.close();
                return true;
            }

            conn.close();
            return false;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public void addPasswordToDb(String username, String password) {
        String sql = "INSERT INTO passwords(username,password) VALUES(?,?)";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Cookie addCookieToDb() {
        Cookie c = new Cookie();
        String sql = "INSERT INTO cookies(cookieId,timestamp) VALUES(?,?)";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getId());
            pstmt.setLong(2, c.getTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return c;
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
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
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

    @Override
    public void createCookieTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS cookies (\n"
                + "	id String PRIMARY KEY,\n"
                + "	timestamp double NOT NULL\n"
                + ");";

        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean checkCookieValid(Cookie c) {
        // TODO Auto-generated method stub
        return false;
    }


}
