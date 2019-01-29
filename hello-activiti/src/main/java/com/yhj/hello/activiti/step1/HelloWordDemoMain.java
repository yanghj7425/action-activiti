package com.yhj.hello.activiti.step1;

import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class HelloWordDemoMain  {


    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWordDemoMain.class);


    public void launch() throws ParseException {

        ProcessEngine processEngine = getProcessEngine();

        ProcessDefinition processDefinition = getProcessDefinition(processEngine);

        ProcessInstance processInstance = startFlowByProcessDefinitionId(processEngine, processDefinition);

        taskProcessing(processEngine, processInstance);

    }

    private void taskProcessing(ProcessEngine processEngine, ProcessInstance processInstance) throws ParseException {
        TaskService taskService = processEngine.getTaskService();

        Scanner scanner = new Scanner(System.in);
        while (processInstance != null && !processInstance.isEnded()) {
            FormService formService = processEngine.getFormService();

            List<Task> tasks = taskService.createTaskQuery().list();

            // iterator each task and processing
            for (Task task : tasks) {
                TaskFormData taskFormData = formService.getTaskFormData(task.getId());
                List<FormProperty> formProperties = taskFormData.getFormProperties();

                Map<String, Object> propertyVariableMap = readVariables(scanner, formProperties);

                taskService.complete(task.getId(), propertyVariableMap);

                for (Map.Entry entry : propertyVariableMap.entrySet()) {
                    LOGGER.info("key {}, value {}", entry.getKey(), entry.getValue());
                }

                LOGGER.info("task name :[{}], task id :[{}]", task.getName(), task.getId());
                //get current processInstance least status
                processInstance = processEngine.getRuntimeService()
                        .createProcessInstanceQuery() // create a query object
                        .processInstanceId(processInstance.getId()) // this way we always can get one object
                        .singleResult();
            }
        }
        scanner.close();

    }

    private Map<String, Object> readVariables(Scanner scanner, List<FormProperty> formProperties) throws ParseException {
        Map<String, Object> propertyVariableMap = new HashMap<>();
        for (FormProperty property : formProperties) {
            String line = readDataFromIO(scanner, property);
            propertyVariableMap.put(property.getId(), line);
        }
        return propertyVariableMap;

    }

    /**
     * @param scanner  readObject
     * @param property form properties
     * @return String
     * @throws ParseException parse date
     */
    private String readDataFromIO(Scanner scanner, FormProperty property) throws ParseException {
        LOGGER.info("Please input  property name  [{}],which property id  [{}] and  property type [{}]", property.getName(), property.getId(), property.getType().getName());
        String line = scanner.nextLine();
        FormType propertyType = property.getType();
        if (StringFormType.class.isInstance(propertyType)) {
            return line;
        }
        if (DateFormType.class.isInstance(propertyType)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.parse(line);
            return line;
        }
        throw new RuntimeException("can not analysis  type");

    }

    /**
     * @param processEngine     process action object
     * @param processDefinition process definition object
     * @return ProcessInstance
     * @description
     */
    private ProcessInstance startFlowByProcessDefinitionId(ProcessEngine processEngine, ProcessDefinition processDefinition) {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        return runtimeService.startProcessInstanceById(processDefinition.getId());
    }

    /**
     * @param processEngine process action object
     * @return ProcessDefinition
     * @description read bmp20.xml file and create processDefinition
     */
    private ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deployment = deploymentBuilder.deploy();
        String deploymentId = deployment.getId();
        return repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

    }

    /**
     * @return ProcessEngine
     * @Description build a process engine this engine is base on memory
     */
    private ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
        return processEngineConfiguration.buildProcessEngine();
    }

}