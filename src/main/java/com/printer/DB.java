package com.printer;

import java.sql.*;

public class DB implements IDB {

    // private static final String dbUrl = "jdbc:sqlite:C:/sqlite/db/printerDB.db";
    private static final String dbUrl = "jdbc:sqlite:/home/lkj/sqlite-tools-linux-x86-3360000/sqlite-tools-linux-x86-3360000/db/printerDB.db";

    public DB() {
        createNewDatabase(dbUrl);

        createCookieTable(dbUrl);
        createPasswordsTable(dbUrl);

        addPasswordToDb(dbUrl, "user1", "hello");
        addPasswordToDb(dbUrl, "user2", "hello");
    }

    @Override
    public boolean authenticateUser(String pass, String username) {

        String sql = "SELECT password FROM passwords WHERE password=? AND username=?";

        try (Connection conn = connect(dbUrl)) {

            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1, pass);
            psmt.setString(2, username);

            ResultSet rs = psmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public void addPasswordToDb(String url, String username, String password) {
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

    @Override
    public Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Override
    public void createNewDatabase(String url) {

        try (Connection conn = this.connect(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void createPasswordsTable(String url) {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS passwords (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	username text NOT NULL,\n"
                + "	password text NOT NULL\n"
                + ");";

        try (Connection conn = this.connect(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void createCookieTable(String url) {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS cookies (\n"
                + "	cookieId String PRIMARY KEY,\n"
                + "	timestamp double NOT NULL\n"
                + ");";

        try (Connection conn = this.connect(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Cookie authenticateCookie(Cookie c) {
        String sql = "SELECT cookieId, timestamp FROM cookies WHERE cookieId=?";
        String url = dbUrl;
        try (Connection conn = connect(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getId());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.executeQuery();
            
            // return rs.next();
            // return rs.getString(arg0)
            if (rs.next()) 
            {
                // while(rs.next()){
                //     long timestamp = rs.getLong("timestamp");
                //     String id = rs.getString("cookieId");
                //     Cookie cookie = new Cookie(id, timestamp);
                //     return cookie;
                // }
                return c;

            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkCookieValid(Cookie c) {
        // TODO Auto-generated method stub
        return false;
    }

    
}
