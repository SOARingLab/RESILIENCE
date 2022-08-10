package top.soaringlab.longtailed.enginebackend;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.soaringlab.longtailed.enginebackend.service.FoodService;
import top.soaringlab.longtailed.enginebackend.service.MessageEventService;
import top.soaringlab.longtailed.enginebackend.service.PizzaService;
import top.soaringlab.longtailed.enginebackend.service.ProcessLogService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GroovyScript {

    private static ProcessLogService processLogService;

    private static MessageEventService messageEventService;

    private static PizzaService pizzaService;

    private static FoodService foodService;

    @Autowired
    public GroovyScript(
            ProcessLogService processLogService,
            MessageEventService messageEventService,
            PizzaService pizzaService,
            FoodService foodService
    ) {
        GroovyScript.processLogService = processLogService;
        GroovyScript.messageEventService = messageEventService;
        GroovyScript.pizzaService = pizzaService;
        GroovyScript.foodService = foodService;
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

//    public static boolean isHighRisk(String area) {
//        return pizzaService.isHighRisk(area);
//    }
//
//    public static boolean isMediumRisk(String area) {
//        return pizzaService.isMediumRisk(area);
//    }
//
//    public static boolean isFivePM() {
//        return false;
//    }
//
//    public static void foodDemandTopic(ProcessInstance processInstance, String community, String item, int amount) {
//        foodService.saveFoodDemand(processInstance.getBusinessKey(), community, item, amount);
//    }
//
//    public static void foodSupplyTopic(ProcessInstance processInstance, String community, String item, int amount) {
//        foodService.saveFoodSupply(processInstance.getBusinessKey(), community, item, amount);
//    }
//
//    public static String risk(ProcessInstance processInstance, String region) {
//        if (region.equals("region_3")) {
//            return "high";
//        } else if (region.equals("region_2")) {
//            return "medium";
//        } else {
//            return "low";
//        }
//    }
//
//    public static double valueExchange(ProcessInstance processInstance, String name, double[] value) {
//        if (name.equals("ap")) {
//            return value[0] - value[1];
//        } else if (name.equals("cs")) {
//            return 100.0 - value[0] + value[1] / 25;
//        } else if (name.equals("lc")) {
//            return value[0] * 20;
//        }
//        return 0;
//    }
}
