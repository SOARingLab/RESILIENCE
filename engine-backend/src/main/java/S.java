import org.camunda.bpm.engine.runtime.ProcessInstance;
import top.soaringlab.longtailed.enginebackend.GroovyScript;

public class S {

    public static void info(ProcessInstance processInstance, String message) {
        GroovyScript.info(processInstance, message);
    }

    public static void warn(ProcessInstance processInstance, String message) {
        GroovyScript.warn(processInstance, message);
    }

//    public static void log(ProcessInstance processInstance, String message) {
//        GroovyScript.log(processInstance, message);
//    }

    public static void sendMessage(ProcessInstance processInstance, String name) {
        GroovyScript.sendMessage(processInstance, name);
    }

    public static void sendMessageToStart(ProcessInstance processInstance, String name) {
        GroovyScript.sendMessageToStart(processInstance, name);
    }

    public static void deleteProcessInstance(ProcessInstance processInstance) {
        GroovyScript.deleteProcessInstance(processInstance);
    }

//    public static boolean isHighRisk(String area) {
//        return GroovyScript.isHighRisk(area);
//    }
//
//    public static boolean isMediumRisk(String area) {
//        return GroovyScript.isMediumRisk(area);
//    }
//
//    public static boolean isFivePM() {
//        return GroovyScript.isFivePM();
//    }
//
//    public static void foodDemandTopic(ProcessInstance processInstance, String community, String item, int amount) {
//        GroovyScript.foodDemandTopic(processInstance, community, item, amount);
//    }
//
//    public static void foodSupplyTopic(ProcessInstance processInstance, String community, String item, int amount) {
//        GroovyScript.foodSupplyTopic(processInstance, community, item, amount);
//    }
//
//    public static String risk(ProcessInstance processInstance, String region) {
//        return GroovyScript.risk(processInstance, region);
//    }
//
//    public static double valueExchange(ProcessInstance processInstance, String name, double... value) {
//        return GroovyScript.valueExchange(processInstance, name, value);
//    }
}
