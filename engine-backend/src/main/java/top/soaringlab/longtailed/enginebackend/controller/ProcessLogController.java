package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.enginebackend.domain.ProcessLog;
import top.soaringlab.longtailed.enginebackend.service.ProcessLogService;

@RestController
@RequestMapping(value = "/api/process-log")
public class ProcessLogController {

    @Autowired
    private ProcessLogService processLogService;

    @GetMapping(value = "/find-all")
    public Page<ProcessLog> findAll(Integer page, Integer size) {
        return processLogService.findAll(page, size);
    }

    @GetMapping(value = "/findByBusinessKey")
    public Page<ProcessLog> findByBusinessKey(String businessKey, Integer page, Integer size) {
        return processLogService.findByBusinessKey(businessKey, page, size);
    }

    @GetMapping(value = "/find-latest")
    public String findLatest() {
        return processLogService.findLatest();
    }
}
