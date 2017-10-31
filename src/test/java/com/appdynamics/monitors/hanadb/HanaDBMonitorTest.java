package com.appdynamics.monitors.hanadb;

import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HanaDBMonitorTest {

    @Test
    public void testHanaDBMonitor() throws TaskExecutionException, InterruptedException {
        Map<String, String> taskArgs = new HashMap<String, String>();
        taskArgs.put("config-file", "src/test/resources/conf/integration-test-config.yml");

        TaskOutput result = new HanaDBMonitor().execute(taskArgs, null);
        assertTrue(result.getStatusMessage().contains("Metric Upload Complete"));
    }
}
