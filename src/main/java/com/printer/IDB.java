package com.printer;

import java.sql.*;

public interface IDB {
    public boolean authenticateUser(String pass, String username);

    void addPasswordToDb(String username, String password);

    public Connection connect();

    public void createNewDatabase();

    public void createPasswordsTable();

    public void createCookieTable();

    public boolean authenticateCookie(Cookie c);

    public Cookie addCookieToDb();
}
