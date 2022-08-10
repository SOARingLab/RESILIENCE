package top.soaringlab.longtailed.enginebackend.controller;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping(value = "/api/muffin")
public class MuffinController {

    @GetMapping(value = "/test")
    public void test() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<>();
        variables.put("bakeDelayed", false);
        runtimeService.startProcessInstanceByKey("Process_0lgydwl", "a", variables);
        variables.put("bakeDelayed", true);
        runtimeService.startProcessInstanceByKey("Process_0lgydwl", "b", variables);
    }

    @GetMapping(value = "/start")
    public void start() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Map<String, Object> variables = new HashMap<>();
            boolean bakeDelayed = random.nextInt(1000) < 500;
            variables.put("bakeDelayed", bakeDelayed);
            runtimeService.startProcessInstanceByKey("Process_0lgydwl", String.valueOf(i), variables);
        }
    }
}
