package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.compilerbackend.service.DeploymentService;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/deployment")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping(value = "/deploy")
    public void deploy(@RequestBody Map<String, String> map) throws Exception {
        deploymentService.deploy(map.get("processId"), map.get("filename"), map.get("file"));
    }

    @PostMapping(value = "/deploy-with-annotations")
    public void deployWithAnnotations(@RequestBody Map<String, String> map) throws Exception {
        deploymentService.deployWithAnnotations(map.get("processId"), map.get("filename"), map.get("file"));
    }
}
