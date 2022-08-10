package top.soaringlab.longtailed.enginebackend.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.springframework.stereotype.Service;

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

    public void migrate(String processDefinitionKey) {
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
        ProcessDefinition sourceProcessDefinition = processDefinitions.get(1);

        MigrationPlan migrationPlan = runtimeService
                .createMigrationPlan(sourceProcessDefinition.getId(), targetProcessDefinition.getId())
                .mapEqualActivities()
                .updateEventTriggers()
                .build();

        ProcessInstanceQuery processInstanceQuery = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionId(sourceProcessDefinition.getId());

        runtimeService.newMigration(migrationPlan)
                .processInstanceQuery(processInstanceQuery)
                .execute();
    }
}
