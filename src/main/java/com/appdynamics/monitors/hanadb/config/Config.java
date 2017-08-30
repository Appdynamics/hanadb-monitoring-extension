package com.appdynamics.monitors.hanadb.config;

import com.appdynamics.monitors.hanadb.exception.HanaDBMonitorException;
import com.google.common.base.Strings;

/**
 * Created by michi on 18.02.17.
 */
public class Config {
    private String host;
    private String port;
    private String encryptionKey;
    private String encryptionPassword;
    private String username;
    private String password;
    private int numberOfThreads;
    private Query[] queries;
    private String metricPathPrefix;
    private String jdbcPrefix;
    private String jdbcOptions;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }

    public String getEncryptionPassword() {
        return encryptionPassword;
    }

    public void setEncryptionPassword(String encryptionPassword) { this.encryptionPassword = encryptionPassword; }

    public String getMetricPathPrefix() {
        return metricPathPrefix;
    }

    public void setMetricPathPrefix(String metricPathPrefix) {
        this.metricPathPrefix = metricPathPrefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Query[] getQueries() {
        return queries;
    }

    public void setQueries(Query[] queries) {
        this.queries = queries;
    }

    public String getJdbcPrefix() { return jdbcPrefix; }

    public void setJdbcPrefix(String jdbcPrefix) { this.jdbcPrefix = jdbcPrefix; }

    public String getJdbcOptions() { return jdbcOptions; }

    public void setJdbcOptions(String jdbcOptions) { this.jdbcOptions = jdbcOptions; }

    public void checkBaseConfig() throws HanaDBMonitorException{
        if (Strings.isNullOrEmpty(getHost()) ||
                Strings.isNullOrEmpty(getPort()) ||
                Strings.isNullOrEmpty(getPassword()) ||
                Strings.isNullOrEmpty(getUsername())) {
            throw new HanaDBMonitorException();
        }
    }
}
