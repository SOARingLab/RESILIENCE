package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;
import top.soaringlab.longtailed.compilerbackend.service.ProcessVariableService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/process-variable")
public class ProcessVariableController {

    @Autowired
    private ProcessVariableService processVariableService;

    @GetMapping(value = "/find-by-process-id/{processId}")
    public List<ProcessVariable> findByProcessId(@PathVariable String processId) {
        return processVariableService.findByProcessId(processId);
    }

    @GetMapping(value = "/get-one/{processVariable}")
    public ProcessVariable getOne(@PathVariable ProcessVariable processVariable) {
        return processVariable;
    }

    @PostMapping(value = "/save")
    public ProcessVariable save(@RequestBody ProcessVariable processVariable) {
        return processVariableService.save(processVariable);
    }

    @DeleteMapping(value = "/delete/{processVariable}")
    public void delete(@PathVariable ProcessVariable processVariable) {
        processVariableService.delete(processVariable);
    }
}
