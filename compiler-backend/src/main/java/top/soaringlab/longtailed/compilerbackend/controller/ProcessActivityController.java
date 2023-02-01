package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessActivity;
import top.soaringlab.longtailed.compilerbackend.service.ProcessActivityService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/process-activity")
public class ProcessActivityController {

    @Autowired
    private ProcessActivityService processActivityService;

    @GetMapping(value = "/find-by-process-id/{processId}")
    public List<ProcessActivity> findByProcessId(@PathVariable String processId) {
        return processActivityService.findByProcessId(processId);
    }

    @GetMapping(value = "/get-one/{processActivity}")
    public ProcessActivity getOne(@PathVariable ProcessActivity processActivity) {
        return processActivity;
    }

    @PostMapping(value = "/save")
    public ProcessActivity save(@RequestBody ProcessActivity processActivity) {
        return processActivityService.save(processActivity);
    }

    @DeleteMapping(value = "/delete/{processActivity}")
    public void delete(@PathVariable ProcessActivity processActivity) {
        processActivityService.delete(processActivity);
    }
}
