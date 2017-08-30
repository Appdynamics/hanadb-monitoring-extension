package com.appdynamics.monitors.hanadb;

import java.sql.*;

/**
 * Created by michi on 18.02.17.
 */
class JDBCConnectionAdapter {
    private final String url;
    private final String username;
    private final String password;

    private JDBCConnectionAdapter(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    static JDBCConnectionAdapter create(String url, String username, String password){
        return new JDBCConnectionAdapter(url, username, password);
    }

    Connection open() throws Exception {
        Connection connection;
        connection = DriverManager.getConnection(url,username,password);
        return connection;
    }

    ResultSet queryDatabase(Connection connection, String query) throws Exception {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    void closeConnection(Connection connection) throws Exception {
        connection.close();
    }
}
