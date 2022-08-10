package top.soaringlab.longtailed.engineactiviti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.soaringlab.longtailed.engineactiviti.dto.StartProcessInstance;
import top.soaringlab.longtailed.engineactiviti.service.DeploymentService;

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
}
