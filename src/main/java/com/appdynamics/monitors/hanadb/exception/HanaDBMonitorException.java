package com.appdynamics.monitors.hanadb.exception;

/**
 * Created by michi on 18.02.17.
 */
public class HanaDBMonitorException extends Exception {

    public HanaDBMonitorException() {
        super("config.yml Parameters are not present");
    }

    public HanaDBMonitorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HanaDBMonitorException(Throwable cause) {
        super(cause);
    }

}
