package com.printer;

import java.sql.*;

public interface IDB {
    public boolean authenticateUser(String pass, String username);

    void addPasswordToDb(String url, String username, String password);

    public Connection connect(String url);

    public void createNewDatabase(String url);

    public void createPasswordsTable(String url);

    public void createCookieTable(String url);
}
