package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.conf.MonitorConfiguration.ConfItem;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;
import com.appdynamics.monitors.hanadb.config.Globals;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class HanaDBMonitor extends AManagedMonitor {
    private static final Logger logger = LoggerFactory.getLogger(HanaDBMonitor.class);
    private MonitorConfiguration configuration;

    public HanaDBMonitor() { logger.info(String.format("Using HanaDB Monitor Version [%s]", getImplementationVersion())); }

    public void initialize(Map<String, String> argsMap) {
        if (configuration == null) {
            MetricWriteHelper metricWriteHelper = MetricWriteHelperFactory.create(this);
            MonitorConfiguration conf = new MonitorConfiguration(Globals.defaultMetricPrefix,
                    new TaskRunnable(), metricWriteHelper);
            final String configFilePath = argsMap.get(Globals.configFile);
            conf.setConfigYml(configFilePath);
            conf.setMetricWriter(MetricWriteHelperFactory.create(this));
            conf.checkIfInitialized(ConfItem.CONFIG_YML, ConfItem.EXECUTOR_SERVICE, ConfItem.METRIC_PREFIX, ConfItem.METRIC_WRITE_HELPER);
            this.configuration = conf;
        }
    }

    public TaskOutput execute(Map<String, String> map, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        try{
            if(map != null){
                if (logger.isDebugEnabled()) {logger.debug("The raw arguments are {}", map);}
                initialize(map);
                configuration.executeTask();
                return new TaskOutput("HanaDB Monitor Metric Upload Complete");
            }
        }
        catch(Exception e) {
            logger.error("Failed to execute the HanaDB monitoring task", e);
        }
        throw new TaskExecutionException("HanaDB monitoring task completed with failures.");
    }

    public class TaskRunnable implements Runnable {
        @SuppressWarnings("unchecked")
        public void run () {
            Map<String, ?> config = configuration.getConfigYml();
            if (config != null) {
                List<Map> queries = (List<Map>) config.get(Globals.queries);
                ArrayList clusters = (ArrayList) config.get(Globals.clusters);
                if (queries != null && !queries.isEmpty() && clusters != null && !clusters.isEmpty()) {
                    for (Map<String, String> cluster : (Iterable<Map<String, String>>) clusters) {
                        for (Map query : queries) {
                            String password = Utilities.getPassword(cluster);
                            String url = cluster.get(Globals.connectionString);
                            if (logger.isDebugEnabled()) { logger.debug("Connection with JDBC Connection String={}", url); }
                            JDBCConnectionAdapter jdbcConnectionAdapter = new JDBCConnectionAdapter(url, cluster.get(Globals.userName), password);
                            HanaDBMonitorTask task = new HanaDBMonitorTask(configuration, jdbcConnectionAdapter, query);
                            configuration.getExecutorService().execute(task);
                        }
                    }
                } else { logger.error("There are no Queries configured."); }
            } else { logger.error("The config.yml is not loaded due to previous errors. The task will not run"); }
        }
    }

    private static String getImplementationVersion() { return HanaDBMonitor.class.getPackage().getImplementationTitle(); }
}