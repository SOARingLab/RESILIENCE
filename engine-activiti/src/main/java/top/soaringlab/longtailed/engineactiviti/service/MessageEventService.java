package top.soaringlab.longtailed.engineactiviti.service;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageEventService {

    private static class MessageWithVariables {
        private String messageName;

        private String businessKey;

        private Map<String, Object> processVariables;

        public MessageWithVariables(String messageName, String businessKey, Map<String, Object> processVariables) {
            this.messageName = messageName;
            this.businessKey = businessKey;
            this.processVariables = processVariables;
        }
    }

    private final List<MessageWithVariables> messageWithVariablesList = new CopyOnWriteArrayList<>();

    public void sendMessage(String messageName, String businessKey, Map<String, Object> processVariables) {

        // Activiti businessKey bug
        businessKey = (String) processVariables.get("businessKey");

        messageWithVariablesList.add(new MessageWithVariables(messageName, businessKey, processVariables));
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void receiveMessageWithVariables() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        for (MessageWithVariables messageWithVariables : messageWithVariablesList) {
            ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                    .messageEventSubscriptionName(messageWithVariables.messageName)

                    // Activiti businessKey bug
                    .processVariableValueEquals("businessKey", messageWithVariables.businessKey);

//                    .processInstanceBusinessKey(messageWithVariables.businessKey);
            if (executionQuery.count() > 0) {
                Execution execution = executionQuery.list().get(0);
                runtimeService.messageEventReceived(messageWithVariables.messageName, execution.getId(), messageWithVariables.processVariables);
                messageWithVariablesList.remove(messageWithVariables);
                break;
            }
        }
    }
}
