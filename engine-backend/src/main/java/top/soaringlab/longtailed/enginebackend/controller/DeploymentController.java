package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.soaringlab.longtailed.enginebackend.dto.MigrateProcessInstance;
import top.soaringlab.longtailed.enginebackend.dto.StartProcessInstance;
import top.soaringlab.longtailed.enginebackend.service.DeploymentService;

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

    @PostMapping(value = "/migrate")
    public void migrate(@RequestBody MigrateProcessInstance migrateProcessInstance) {
        deploymentService.migrate(
                migrateProcessInstance.getProcessDefinitionKey()
        );
    }
}
