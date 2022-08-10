package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessModel;
import top.soaringlab.longtailed.compilerbackend.service.ProcessModelService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/process-model")
public class ProcessModelController {

    @Autowired
    private ProcessModelService processModelService;

    @GetMapping(value = "/find-by-process-id/{processId}")
    public List<ProcessModel> findByProcessId(@PathVariable String processId) {
        return processModelService.findByProcessId(processId);
    }

    @GetMapping(value = "/get-one/{processModel}")
    public ProcessModel getOne(@PathVariable ProcessModel processModel) {
        return processModel;
    }

    @PostMapping(value = "/save")
    public ProcessModel save(@RequestBody ProcessModel processModel) {
        return processModelService.save(processModel);
    }

    @DeleteMapping(value = "/delete/{processModel}")
    public void delete(@PathVariable ProcessModel processModel) {
        processModelService.delete(processModel);
    }
}
