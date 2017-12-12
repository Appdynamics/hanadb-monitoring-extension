package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.monitors.hanadb.config.Globals;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

class HanaDBMonitorTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HanaDBMonitorTask.class);
    private final Map query;
    private final MonitorConfiguration configuration;
    private final JDBCConnectionAdapter jdbcConnectionAdapter;

    HanaDBMonitorTask(MonitorConfiguration configuration, JDBCConnectionAdapter jdbcConnectionAdapter, Map query) {
        this.jdbcConnectionAdapter = jdbcConnectionAdapter;
        this.configuration = configuration;
        this.query = query;
    }

    public void run() {
        try {
            runTask();
        } catch (Exception e) {
            configuration.getMetricWriter().registerError(e.getMessage(), e);
            logger.error("Error while running the task", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void runTask() {
        try {
            MetricPrinter metricPrinter = new MetricPrinter(configuration.getMetricWriter());
            Connection connection = jdbcConnectionAdapter.open(Utilities.getJdbcDriverClass(configuration.getConfigYml()));
            if (logger.isDebugEnabled()) {
                logger.debug("Calling DB ={}", jdbcConnectionAdapter.toString());
            }
            ResultSet resultSet = jdbcConnectionAdapter.queryDatabase(connection, (String) query.get(Globals.statement));
            while (resultSet.next()) {
                String metricName = configuration.getMetricPrefix();
                ArrayList columns = (ArrayList) query.get(Globals.columns);
                for (Map<String, String> current : (Iterable<Map<String, String>>) columns) {
                    if (current.get(Globals.type).equals(Globals.name)) {
                        metricName = metricName + Globals.METRIC_SEPARATOR + resultSet.getString(current.get(Globals.name));
                    } else if (current.get(Globals.type).equals(Globals.metric)) {
                        if (!Strings.isNullOrEmpty(current.get(Globals.convertFrom)) && !Strings.isNullOrEmpty(current.get(Globals.convertTo))) {
                            metricPrinter.reportMetric(metricName +
                                            Globals.METRIC_SEPARATOR +
                                            current.get(Globals.name),
                                    Utilities.convert(current.get(Globals.convertFrom),
                                            current.get(Globals.convertTo),
                                            resultSet.getBigDecimal(current.get(Globals.name))));
                        } else {
                            metricPrinter.reportMetric(metricName +
                                            Globals.METRIC_SEPARATOR +
                                            current.get(Globals.name),
                                    resultSet.getBigDecimal(current.get(Globals.name)));
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Sending metric={}", metricName);
                        }
                    } else {
                        logger.error("Invalid Type");
                    }
                }
            }
            jdbcConnectionAdapter.closeConnection(connection);
            logger.info("Finished working on Result Set");
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
