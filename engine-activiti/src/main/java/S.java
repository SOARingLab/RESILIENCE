import org.activiti.engine.runtime.ProcessInstance;
import top.soaringlab.longtailed.engineactiviti.GroovyScript;

public class S {

    public static void info(ProcessInstance processInstance, String message) {
        GroovyScript.info(processInstance, message);
    }

    public static void warn(ProcessInstance processInstance, String message) {
        GroovyScript.warn(processInstance, message);
    }

    public static void sendMessage(ProcessInstance processInstance, String name) {
        GroovyScript.sendMessage(processInstance, name);
    }

    public static void sendMessageToStart(ProcessInstance processInstance, String name) {
        GroovyScript.sendMessageToStart(processInstance, name);
    }

    public static void deleteProcessInstance(ProcessInstance processInstance) {
        GroovyScript.deleteProcessInstance(processInstance);
    }
}
