package com.appdynamics.monitors.hanadb;

import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.monitors.hanadb.config.Globals;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class HanaDBMonitorTest {

    @Test
    public void testHanaDBMonitor() throws TaskExecutionException, InterruptedException {
        Map<String, String> taskArgs = new HashMap<String, String>();
        taskArgs.put("config-file", "src/test/resources/conf/integration-test-config.yml");

        TaskOutput result = new HanaDBMonitor().execute(taskArgs, null);
        assertTrue(result.getStatusMessage().contains("Metric Upload Complete"));
    }

    @Test
    public void testHanaDBMonitorTask() throws Exception {
        MetricWriteHelper writer = Mockito.mock(MetricWriteHelper.class);
        Runnable runner = Mockito.mock(Runnable.class);
        MonitorConfiguration conf = new MonitorConfiguration(Globals.defaultMetricPrefix, runner, writer);
        conf.setConfigYml("src/test/resources/conf/integration-test-config.yml");
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] args = invocationOnMock.getArguments();
                System.out.println(args[0] + "=" + args[1]);
                return null;
            }
        }).when(writer).printMetric(Mockito.anyString(), Mockito.any(BigDecimal.class), Mockito.anyString());
        conf.setMetricWriter(writer);
        Map<String,?> config = conf.getConfigYml();
        List<Map> queries = (List<Map>) config.get(Globals.queries);
        ArrayList clusters = (ArrayList) config.get(Globals.clusters);
        if (queries != null && !queries.isEmpty() && clusters != null && !clusters.isEmpty()) {
            for (Map<String, String> cluster : (Iterable<Map<String, String>>) clusters) {
                for (Map query : queries) {
                    String password = Utilities.getPassword(cluster);
                    String url = cluster.get(Globals.connectionString);
                    JDBCConnectionAdapter jdbcConnectionAdapter = new JDBCConnectionAdapter(url, (String) cluster.get(Globals.userName), password);
                    HanaDBMonitorTask task = new HanaDBMonitorTask(conf, jdbcConnectionAdapter, query);
                    conf.getExecutorService().execute(task);
                }
            }
        }
        conf.getExecutorService().awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test(expected = TaskExecutionException.class)
    public void testHanaDBMonitorTaskExcecutionException() throws Exception {
        new HanaDBMonitor().execute(null, null);
    }

   @Test
   public void testHanaDBQuery() throws Exception {
       MetricWriteHelper writer = Mockito.mock(MetricWriteHelper.class);
       Runnable runner = Mockito.mock(Runnable.class);
       MonitorConfiguration conf = new MonitorConfiguration(Globals.defaultMetricPrefix, runner, writer);
       conf.setConfigYml("src/test/resources/conf/integration-test-config.yml");
       Map<String,?> config = conf.getConfigYml();
       List<Map> queries = (List<Map>) config.get(Globals.queries);
       ArrayList clusters = (ArrayList) config.get(Globals.clusters);
       if (queries != null && !queries.isEmpty() && clusters != null && !clusters.isEmpty()) {
           for (Map<String, String> cluster : (Iterable<Map<String, String>>) clusters) {
               for (Map query : queries) {
                   String password = Utilities.getPassword(cluster);
                   String url = cluster.get(Globals.connectionString);
                   JDBCConnectionAdapter jdbcConnectionAdapter = new JDBCConnectionAdapter(url, (String) cluster.get(Globals.userName), password);
                   Connection conn = jdbcConnectionAdapter.open(Utilities.getJdbcDriverClass(config));
                   ResultSet rs = jdbcConnectionAdapter.queryDatabase(conn, "select * from M_DISK_USAGE where USED_SIZE >= 0");
                   while (rs.next()) {
                       assertTrue(!rs.wasNull());
                   }
               }
           }
       }
   }
}
