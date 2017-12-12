package com.appdynamics.monitors.hanadb;

import java.sql.*;

class JDBCConnectionAdapter {
    private final String url;
    private final String username;
    private final String password;

    JDBCConnectionAdapter(String url, String username, String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    Connection open(String jdbcDriverClass) throws SQLException, ClassNotFoundException {
        Class.forName(jdbcDriverClass);
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
