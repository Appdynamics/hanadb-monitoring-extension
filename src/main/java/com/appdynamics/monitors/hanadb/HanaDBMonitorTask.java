package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.monitors.hanadb.config.*;
import com.google.common.base.Strings;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.appdynamics.monitors.hanadb.config.Globals.*;

/**
 * Created by michi on 18.02.17.
 */
class HanaDBMonitorTask implements Runnable {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HanaDBMonitorTask.class);
    private Query query;
    private MetricWriteHelper metricWriteHelper;
    private String metricPrefix;
    private JDBCConnectionAdapter jdbcConnectionAdapter;

    private HanaDBMonitorTask(){}

    public void run() {
        MetricPrinter metricPrinter = new MetricPrinter(metricWriteHelper);
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = jdbcConnectionAdapter.open();
            logger.debug("Calling DB ={}", jdbcConnectionAdapter.toString());
            resultSet = jdbcConnectionAdapter.queryDatabase(connection, query.getStatement());
            while(resultSet.next()){
                String metricName = metricPrefix;
                for(Column column:query.getColumns()){
                    if (column.getType().equals("name")) {
                        metricName = metricName + METRIC_SEPARATOR + resultSet.getString(column.getName());
                    }
                    else if (column.getType().equals("metric")) {
                        if(!Strings.isNullOrEmpty(column.getConvertFrom()) && !Strings.isNullOrEmpty(column.getConvertTo())){
                            metricPrinter.reportMetric(metricName + METRIC_SEPARATOR + column.getName(),
                                            Utilities.convert(column.getConvertFrom(),
                                            column.getConvertTo(),resultSet.getBigDecimal(column.getName())));
                        }
                        else {
                            metricPrinter.reportMetric(metricName + METRIC_SEPARATOR + column.getName(), resultSet.getBigDecimal(column.getName()));
                        }
                        logger.debug("Sending metric={}", metricName);
                    }
                    else {
                        logger.error("Invalid Type");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("DB Connection failed", e);
        } finally {
            try {
                resultSet.close();
                jdbcConnectionAdapter.closeConnection(connection);
            }
            catch (Exception e) {
                logger.error("Unable to close the connection",e);
            }
        }
    }

    public static class Builder {
        private final HanaDBMonitorTask task = new HanaDBMonitorTask();

        Builder metricPrefix (String metricPrefix) {
            task.metricPrefix = metricPrefix;
            return this;
        }

        Builder metricWriter (MetricWriteHelper metricWriteHelper) {
            task.metricWriteHelper = metricWriteHelper;
            return this;
        }

        Builder query (Query query) {
            task.query = query;
            return this;
        }

        Builder jdbcConnectionAdapter (JDBCConnectionAdapter jdbcConnectionAdapter) {
            task.jdbcConnectionAdapter = jdbcConnectionAdapter;
            return this;
        }

        HanaDBMonitorTask build () {
            return task;
        }
    }
}
