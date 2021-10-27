package com.printer;

import java.sql.*;

import org.apache.commons.codec.digest.DigestUtils;

public class DB implements IDB {

    // private static final String dbUrl = "jdbc:sqlite:C:/sqlite/db/printerDB.db";
    private static final String dbUrl = "jdbc:sqlite:/Users/lkj/Downloads/sqlite-tools-osx-x86-3360000/db/printerDB.db";

    public DB() {
        createNewDatabase();

        createCookieTable();
        createPasswordsTable();

        addPasswordToDb("user1", "hello1");
        addPasswordToDb("user2", "hello2");
    }

    @Override
    public boolean authenticateUser(String pass, String username) {

        String sql = "SELECT password FROM passwords WHERE password=? AND username=?";
        String pass_sha256hex = DigestUtils.sha256Hex(pass);

        try (Connection conn = connect()) {
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1, pass_sha256hex);
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

        String sql = "SELECT id FROM cookies WHERE id=?";

        try (Connection conn = connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getId());
            pstmt.executeQuery();

            ResultSet rs = pstmt.executeQuery();
            String cId = rs.getString("id");
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
    public Cookie addCookieToDb() {
        Cookie c = new Cookie();
        String sql = "INSERT INTO cookies(id,timestamp) VALUES(?,?)";

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

}
