package top.soaringlab.longtailed.compilerbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;
import top.soaringlab.longtailed.compilerbackend.service.ControllabilityModelService;

@RestController
@RequestMapping(value = "/api/controllability-model")
public class ControllabilityModelController {

    @Autowired
    private ControllabilityModelService controllabilityModelService;

    @GetMapping(value = "/find-by-process-id/{processId}")
    public List<ControllabilityModel> findByProcessId(@PathVariable String processId) {
        return controllabilityModelService.findByProcessId(processId);
    }

    @GetMapping(value = "/get-one/{controllabilityModel}")
    public ControllabilityModel getOne(@PathVariable ControllabilityModel controllabilityModel) {
        return controllabilityModel;
    }

    @PostMapping(value = "/save")
    public ControllabilityModel save(@RequestBody ControllabilityModel controllabilityModel) {
        return controllabilityModelService.save(controllabilityModel);
    }

    @DeleteMapping(value = "/delete/{controllabilityModel}")
    public void delete(@PathVariable ControllabilityModel controllabilityModel) {
        controllabilityModelService.delete(controllabilityModel);
    }
}
