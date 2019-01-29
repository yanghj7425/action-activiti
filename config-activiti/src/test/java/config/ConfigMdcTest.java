package config;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.logging.LogMDC;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class ConfigMdcTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigMdcTest.class);


    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();


    @Test
    @Deployment(resources = {"mdc-process.bpmn20.xml"})
    public void ruleUsageExample() {
        LogMDC.setMDCEnabled(true);
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("second_approve");
        LOGGER.info("processInstance name is {}", processInstance.getName());

        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}
