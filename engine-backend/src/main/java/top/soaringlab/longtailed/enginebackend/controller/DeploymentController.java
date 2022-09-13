package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.soaringlab.longtailed.enginebackend.dto.GetProcessInstance;
import top.soaringlab.longtailed.enginebackend.dto.StartProcessInstance;
import top.soaringlab.longtailed.enginebackend.service.DeploymentService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping(value = "/api/deployment")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping(value = "/create")
    public void create(MultipartFile data) throws Exception {
        Scanner scanner = new Scanner(data.getInputStream());
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine()).append("\n");
        }
        deploymentService.create(data.getOriginalFilename(), stringBuilder.toString());
    }

    @PostMapping(value = "/start")
    public void start(@RequestBody StartProcessInstance startProcessInstance) {
        deploymentService.start(
                startProcessInstance.getProcessDefinitionKey(),
                startProcessInstance.getBusinessKey(),
                startProcessInstance.getVariables()
        );
    }

    @GetMapping(value = "/find-old-process-instance")
    public List<String> findOldProcessInstance(String processDefinitionKey) {
        return deploymentService.findOldProcessInstance(processDefinitionKey);
    }

    @GetMapping(value = "/get-process-instance")
    public GetProcessInstance getProcessInstance(String processInstanceId) {
        return deploymentService.getProcessInstance(processInstanceId);
    }

    @PostMapping(value = "/migrate-all")
    public Integer migrateAll(@RequestBody Map<String, String> map) {
        return deploymentService.migrateAll(
                map.get("processDefinitionKey")
        );
    }

    @PostMapping(value = "/migrate-one")
    public Integer migrateOne(@RequestBody Map<String, String> map) {
        return deploymentService.migrateOne(
                map.get("processDefinitionKey"),
                map.get("processInstanceId")
        );
    }
}
