package top.soaringlab.longtailed.engineactiviti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.soaringlab.longtailed.engineactiviti.service.MessageEventService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GroovyScript {

    private static MessageEventService messageEventService;

    @Autowired
    public GroovyScript(
            MessageEventService messageEventService
            ) {
        GroovyScript.messageEventService = messageEventService;
    }

    public static void info(ProcessInstance processInstance, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " INFO : " + message);
    }

    public static void warn(ProcessInstance processInstance, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " WARN : " + message);
    }

    public static void sendMessage(ProcessInstance processInstance, String name) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        messageEventService.sendMessage(name, processInstance.getBusinessKey(), runtimeService.getVariables(processInstance.getId()));
    }

    public static void sendMessageToStart(ProcessInstance processInstance, String name) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByMessage(name, processInstance.getBusinessKey(), runtimeService.getVariables(processInstance.getId()));
    }

    public static void deleteProcessInstance(ProcessInstance processInstance) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(), "");
    }
}
