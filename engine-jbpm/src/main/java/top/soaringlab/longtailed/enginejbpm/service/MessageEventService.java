package top.soaringlab.longtailed.enginejbpm.service;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageEventService {

    private static class MessageWithVariables {
        private String messageName;

        private KieRuntime kieRuntime;

        private Map<String, Object> processVariables;

        public MessageWithVariables(String messageName, KieRuntime kieRuntime, Map<String, Object> processVariables) {
            this.messageName = messageName;
            this.kieRuntime = kieRuntime;
            this.processVariables = processVariables;
        }
    }

    private final List<MessageWithVariables> messageWithVariablesList = new CopyOnWriteArrayList<>();

    public void sendMessage(String messageName, KieRuntime kieRuntime, Map<String, Object> processVariables) {
        messageWithVariablesList.add(new MessageWithVariables(messageName, kieRuntime, processVariables));
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void receiveMessageWithVariables() {
        for (MessageWithVariables messageWithVariables : messageWithVariablesList) {
            for (ProcessInstance processInstance : messageWithVariables.kieRuntime.getProcessInstances()) {
                String[] eventTypes = processInstance.getEventTypes();
                String eventType = "Message-" + messageWithVariables.messageName;
                if (Arrays.asList(eventTypes).contains(eventType)) {
                    processInstance.signalEvent(eventType, null);
                    return;
                }
            }
        }
    }
}
