package com.appdynamics.monitors.hanadb.config;

/**
 * Created by michi on 18.02.17.
 */
public class Query {
    private String statement;
    private Column[] columns;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
    }
}
