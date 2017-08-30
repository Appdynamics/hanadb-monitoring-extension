package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;
import com.appdynamics.monitors.hanadb.config.Config;
import com.appdynamics.monitors.hanadb.config.Query;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.appdynamics.monitors.hanadb.Utilities.convertToString;

/**
 * Created by michi on 18.02.17.
 */
public class HanaDBMonitor extends AManagedMonitor {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HanaDBMonitor.class);
    private boolean initialized = false;
    private Config config;
    private ExecutorService executor;
    private MetricWriteHelper metricWriteHelper;

    public HanaDBMonitor() { logger.info(String.format("Using HanaDB Monitor Version [%s]", getImplementationVersion())); }

    private void initialize(Map<String, String> args) throws Exception {
        if (!initialized) {
            logger.debug("The raw arguments are {}", args);
            metricWriteHelper = MetricWriteHelperFactory.create(this);
            Yaml yaml = new Yaml();
            String configFilename = getConfigFilename(args.get("config-file"));
            FileInputStream fis = new FileInputStream(configFilename);
            Config config = yaml.loadAs(fis, Config.class);
            config.checkBaseConfig();
            this.config = config;
            initialized = true;
        }
    }

    public TaskOutput execute(Map<String, String> args, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        try {
            if (!initialized) { initialize(args); }
            executor = Executors.newFixedThreadPool(config.getNumberOfThreads());
            executor.execute(new TaskRunner());
            logger.info("Finished HanaDB monitor execution");
            return new TaskOutput("Finished HanaDB monitor execution");
        } catch (Exception e) {
            throw new TaskExecutionException("Failed to execute the Postgres monitoring task" + e);
        }
    }

    private class TaskRunner implements Runnable {
        public void run () {
            if (!initialized) { logger.info("HanaDB Monitor is still initializing"); return; }
            if (config != null) {
                Query[] queries = config.getQueries();
                if (queries != null) {
                    for (Query query : queries) {
                        try {
                            HanaDBMonitorTask task = createTask(query);
                            executor.execute(task);
                        } catch (Exception e) { logger.error("Cannot construct Query for {}", convertToString(query.getStatement(), "")); }
                    }
                } else { logger.error("There are no Queries configured."); }
            } else { logger.error("The config.yml is not loaded due to previous errors. The task will not run"); }
        }
    }

    private HanaDBMonitorTask createTask (Query query) {
        String password = Utilities.getPassword(config);
        String url = Utilities.getURL(config);
        JDBCConnectionAdapter jdbcConnectionAdapter = JDBCConnectionAdapter.create(url,config.getUsername(),password);
        return new HanaDBMonitorTask.Builder().
                metricWriter(metricWriteHelper).
                metricPrefix(config.getMetricPathPrefix()).
                jdbcConnectionAdapter(jdbcConnectionAdapter).
                query(query).build();
    }

    private static String getImplementationVersion() { return HanaDBMonitor.class.getPackage().getImplementationTitle(); }

    private String getConfigFilename(String filename) {
        if (filename == null) { return ""; }
        if (new File(filename).exists()) { return filename; }
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) { configFileName = jarPath + File.separator + filename; }
        return configFileName;
    }
}