package top.soaringlab.longtailed.enginebackend;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.soaringlab.longtailed.enginebackend.service.MessageEventService;
import top.soaringlab.longtailed.enginebackend.service.ProcessLogService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GroovyScript {

    private static ProcessLogService processLogService;

    private static MessageEventService messageEventService;

    @Autowired
    public GroovyScript(
            ProcessLogService processLogService,
            MessageEventService messageEventService
    ) {
        GroovyScript.processLogService = processLogService;
        GroovyScript.messageEventService = messageEventService;
    }

    public static void info(ProcessInstance processInstance, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " INFO : " + message);
        processLogService.log(processInstance.getBusinessKey(), message);
    }

    public static void warn(ProcessInstance processInstance, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " WARN : " + message);
        processLogService.log(processInstance.getBusinessKey(), message);
    }

//    public static void log(ProcessInstance processInstance, String message) {
//        processLogService.log(processInstance.getBusinessKey(), message);
//    }

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
