package top.soaringlab.longtailed.enginejbpm;

import org.drools.core.spi.ProcessContext;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.soaringlab.longtailed.enginejbpm.dto.PublicApi;
import top.soaringlab.longtailed.enginejbpm.service.MessageEventService;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class GroovyScript {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageEventService messageEventService;

    public void info(ProcessContext processContext, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " INFO : " + message);
    }

    public void warn(ProcessContext processContext, String message) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        System.out.println(date + " WARN : " + message);
    }

    public void sendMessage(ProcessContext processContext, String name) {
        KieRuntime kieRuntime = processContext.getKieRuntime();
        messageEventService.sendMessage(name, kieRuntime, null);
    }

    public void sendMessageToStart(ProcessContext processContext, String name) {
        String startProcessId = (String) processContext.getVariable("startProcessId");

        List<String> variableNames = Arrays.asList("S", "order_status", "region", "delivery_method", "amount", "fake_risk_level");
        Map<String, Object> processVariables = getProcessVariables(processContext, variableNames);

        KieRuntime kieRuntime = processContext.getKieRuntime();
        kieRuntime.startProcess(startProcessId, processVariables);

        messageEventService.sendMessage(name, kieRuntime, null);
    }

    public void deleteProcessInstance(ProcessContext processContext) {
        KieRuntime kieRuntime = processContext.getKieRuntime();
        for (ProcessInstance processInstance : kieRuntime.getProcessInstances()) {
            kieRuntime.abortProcessInstance(processInstance.getId());
        }
    }

    public void callPublicApi(ProcessContext processContext, long publicApiId) throws Exception {
        PublicApi publicApi = restTemplate.getForObject("http://localhost:8088/api/public-api/get-one/" + publicApiId, PublicApi.class);

        String query = "?";
        Map<String, Object> input = new HashMap<>();
        for (int i = 0; i < publicApi.getInputFroms().size(); i++) {
            if (i > 0) {
                query += "&";
            }
            query += publicApi.getInputTos().get(i) + "={" + publicApi.getInputTos().get(i) + "}";
            input.put(publicApi.getInputTos().get(i), processContext.getVariable(publicApi.getInputFroms().get(i)));
        }

        Map<String, Object> output;
        if ("GET".equals(publicApi.getMethod())) {
            output = restTemplate.getForObject(publicApi.getUrl() + query, Map.class, input);
        } else {
            output = restTemplate.postForObject(publicApi.getUrl(), input, Map.class);
        }
        for (int i = 0; i < publicApi.getOutputFroms().size(); i++) {
            processContext.setVariable(publicApi.getOutputFroms().get(i), output.get(publicApi.getOutputTos().get(i)));
        }
    }

    private Map<String, Object> getProcessVariables(ProcessContext processContext, List<String> variableNames) {
        Map<String, Object> processVariables = new HashMap<>();
        for (String variableName : variableNames) {
            processVariables.put(variableName, processContext.getVariable(variableName));
        }
        return processVariables;
    }
}
