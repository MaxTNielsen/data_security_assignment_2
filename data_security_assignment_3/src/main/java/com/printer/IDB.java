package com.printer;

import java.sql.*;

public interface IDB {
    boolean authenticateUser(String pass, String username);

    void addPasswordToDb(String username, String password);

    Connection connect();

    void createNewDatabase();

    void createPasswordsTable();
}
