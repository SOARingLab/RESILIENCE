package top.soaringlab.longtailed.enginebackend.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExperimentService {

    private int numFrontEnd = 0;

    private List<Boolean> requirementHappenList;
    private List<Boolean> responseFinishList;
    private List<Boolean> reportList;
    private int reportIndex;

    public ExperimentService() {
        requirementHappenList = new CopyOnWriteArrayList<>();
        responseFinishList = new CopyOnWriteArrayList<>();
        reportList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numFrontEnd; i++) {
            requirementHappenList.add(false);
            responseFinishList.add(false);
            reportList.add(false);
        }
        reportIndex = -1;
    }

    // @Scheduled(initialDelay = 5000, fixedDelay = 120000)
    public void experiment() {
        stop();
        numFrontEnd++;
        System.out.println("--------experiment start," + numFrontEnd);
        start();
    }

    public void start() {
        requirementHappenList = new CopyOnWriteArrayList<>();
        responseFinishList = new CopyOnWriteArrayList<>();
        reportList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numFrontEnd; i++) {
            requirementHappenList.add(false);
            responseFinishList.add(false);
            reportList.add(false);
        }
        reportIndex = -1;

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        for (int i = 0; i < numFrontEnd; i++) {
            runtimeService.startProcessInstanceByKey("Process_04y7zv3", String.valueOf(i));
            runtimeService.startProcessInstanceByKey("Process_017rs0h", String.valueOf(i));
        }

        runtimeService.startProcessInstanceByKey("Process_1mjyvjg", "0");
    }

    public void stop() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        for (ProcessInstance processInstance : processInstanceQuery.list()) {
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "");
        }
    }

    public void sendMessage(String messageName, String businessKey) {
        int index = Integer.parseInt(businessKey);
        if (messageName.equals("Requirement happen")) {
            requirementHappenList.set(index, true);
        } else if (messageName.equals("Response finish")) {
            responseFinishList.set(reportIndex, true);
        } else if (messageName.equals("Report")) {
            reportList.set(index, true);
        }
    }

    // @Scheduled(initialDelay = 200, fixedDelay = 200)
    public void receiveMessage() {
        String executionId;

        for (int i = 0; i < numFrontEnd; i++) {
            if (requirementHappenList.get(i)) {
                executionId = engineQueryMessage("Process_017rs0h", String.valueOf(i), "Requirement happen");
                if (executionId != null) {
                    requirementHappenList.set(i, false);
                    engineReceiveMessage(executionId, "Requirement happen");
                }
            }
            if (responseFinishList.get(i)) {
                executionId = engineQueryMessage("Process_017rs0h", String.valueOf(i), "Response finish");
                if (executionId != null) {
                    responseFinishList.set(i, false);
                    engineReceiveMessage(executionId, "Response finish");
                }
            }
        }

        executionId = engineQueryMessage("Process_1mjyvjg", "0", "Report received");
        if (executionId != null) {
            for (int i = 0; i < numFrontEnd; i++) {
                reportIndex = (reportIndex + 1) % numFrontEnd;
                if (reportList.get(reportIndex)) {
                    break;
                }
            }
            if (reportList.get(reportIndex)) {
                reportList.set(reportIndex, false);
                engineReceiveMessage(executionId, "Report received");
            }
        }
    }

    private String engineQueryMessage(String processDefinitionKey, String businessKey, String messageName) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                .processDefinitionKey(processDefinitionKey)
                .processInstanceBusinessKey(businessKey)
                .messageEventSubscriptionName(messageName);
        if (executionQuery.count() > 0) {
            Execution execution = executionQuery.list().get(0);
            return execution.getId();
        } else {
            return null;
        }
    }

    private void engineReceiveMessage(String executionId, String messageName) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.messageEventReceived(messageName, executionId);
    }
}
