package top.soaringlab.longtailed.engineactiviti.service;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;

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

        // Activiti businessKey bug
        variables.put("businessKey", businessKey);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }
}
