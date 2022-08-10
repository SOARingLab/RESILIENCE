package top.soaringlab.longtailed.enginebackend.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.soaringlab.longtailed.enginebackend.domain.MessageEvent;
import top.soaringlab.longtailed.enginebackend.repository.MessageEventRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional
public class MessageEventService {

    @Autowired
    private MessageEventRepository messageEventRepository;

    public List<MessageEvent> findAll() {
        return messageEventRepository.findAll();
    }

    public void sendMessage(String name, String businessKey) {
        messageEventRepository.save(new MessageEvent(name, businessKey));
    }

//    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void receiveMessage() {
        List<MessageEvent> messageEventList = messageEventRepository.findAll();
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        for (MessageEvent messageEvent : messageEventList) {
            ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                    .messageEventSubscriptionName(messageEvent.getName())
                    .processInstanceBusinessKey(messageEvent.getBusinessKey());
            if (executionQuery.count() > 0) {
                Execution execution = executionQuery.list().get(0);
                runtimeService.messageEventReceived(messageEvent.getName(), execution.getId());
                messageEventRepository.delete(messageEvent);
                break;
            }
        }
    }

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
        messageWithVariablesList.add(new MessageWithVariables(messageName, businessKey, processVariables));
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void receiveMessageWithVariables() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        for (MessageWithVariables messageWithVariables : messageWithVariablesList) {
            ExecutionQuery executionQuery = runtimeService.createExecutionQuery()
                    .messageEventSubscriptionName(messageWithVariables.messageName)
                    .processInstanceBusinessKey(messageWithVariables.businessKey);
            if (executionQuery.count() > 0) {
                Execution execution = executionQuery.list().get(0);
                runtimeService.messageEventReceived(messageWithVariables.messageName, execution.getId(), messageWithVariables.processVariables);
                messageWithVariablesList.remove(messageWithVariables);
                break;
            }
        }
    }
}
