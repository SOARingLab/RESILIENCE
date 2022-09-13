package top.soaringlab.longtailed.enginebackend.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.enginebackend.dto.GetProcessInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DeploymentService {

    public void create(String filename, String file) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                .addString(filename, file)
                .deploy();
    }

    public void start(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    public List<String> findOldProcessInstance(String processDefinitionKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        List<String> processInstanceIds = new ArrayList<>();
        for (int i = 1; i < processDefinitions.size(); i++) {
            ProcessDefinition sourceProcessDefinition = processDefinitions.get(i);

            List<ProcessInstance> processInstances = runtimeService
                    .createProcessInstanceQuery()
                    .processDefinitionId(sourceProcessDefinition.getId())
                    .list();

            for (ProcessInstance processInstance : processInstances) {
                processInstanceIds.add(processInstance.getId());
            }
        }

        return processInstanceIds;
    }

    public GetProcessInstance getProcessInstance(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        ProcessInstance processInstance = processInstances.get(0);

        List<VariableInstance> variableInstances = runtimeService
                .createVariableInstanceQuery()
                .processInstanceIdIn(processInstanceId)
                .list();

        GetProcessInstance getProcessInstance = new GetProcessInstance();
        getProcessInstance.setId(processInstanceId);
        getProcessInstance.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        getProcessInstance.setBusinessKey(processInstance.getBusinessKey());

        for (VariableInstance variableInstance : variableInstances) {
            getProcessInstance.getVariables().put(variableInstance.getName(), variableInstance.getValue());
        }

        return getProcessInstance;
    }

    public int migrateAll(String processDefinitionKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        ProcessDefinition targetProcessDefinition = processDefinitions.get(0);

        int numMigrate = 0;
        for (int i = 1; i < processDefinitions.size(); i++) {
            ProcessDefinition sourceProcessDefinition = processDefinitions.get(i);

            MigrationPlan migrationPlan = runtimeService
                    .createMigrationPlan(sourceProcessDefinition.getId(), targetProcessDefinition.getId())
                    .mapEqualActivities()
                    .updateEventTriggers()
                    .build();

            List<ProcessInstance> processInstances = runtimeService
                    .createProcessInstanceQuery()
                    .processDefinitionId(sourceProcessDefinition.getId())
                    .list();

            List<String> processInstanceIds = new ArrayList<>();
            for (ProcessInstance processInstance : processInstances) {
                // todo
                numMigrate++;
                processInstanceIds.add(processInstance.getId());
            }

            if (!processInstanceIds.isEmpty()) {
                runtimeService.newMigration(migrationPlan)
                        .processInstanceIds(processInstanceIds)
                        .execute();
            }
        }

        return numMigrate;
    }

    public int migrateOne(String processDefinitionKey, String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        ProcessDefinition targetProcessDefinition = processDefinitions.get(0);

        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
        ProcessInstance processInstance = processInstances.get(0);

        // todo

        MigrationPlan migrationPlan = runtimeService
                .createMigrationPlan(processInstance.getProcessDefinitionId(), targetProcessDefinition.getId())
                .mapEqualActivities()
                .updateEventTriggers()
                .build();

        runtimeService.newMigration(migrationPlan)
                .processInstanceIds(processInstance.getId())
                .execute();

        return 1;
    }
}
