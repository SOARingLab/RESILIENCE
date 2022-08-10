package top.soaringlab.longtailed.enginejbpm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.soaringlab.longtailed.enginejbpm.dto.StartProcessInstance;
import top.soaringlab.longtailed.enginejbpm.service.DeployService;

import java.util.Scanner;

@RestController
@RequestMapping(value = "/api/deployment")
public class DeployController {

    @Autowired
    private DeployService deployService;

    @PostMapping(value = "/create")
    public void create(MultipartFile data) throws Exception {
        Scanner scanner = new Scanner(data.getInputStream());
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine()).append("\n");
        }
        deployService.create(data.getOriginalFilename(), stringBuilder.toString());
    }

    @PostMapping(value = "/start")
    public void start(@RequestBody StartProcessInstance startProcessInstance) {
        deployService.start(
                startProcessInstance.getProcessDefinitionKey(),
                startProcessInstance.getBusinessKey(),
                startProcessInstance.getVariables()
        );
    }
}
