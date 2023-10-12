package top.soaringlab.longtailed.enginebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.soaringlab.longtailed.enginebackend.service.ExperimentService;

@RestController
@RequestMapping(value = "/api/experiment")
public class ExperimentController {

    @Autowired
    private ExperimentService experimentService;

    @GetMapping(value = "/start")
    public void start() {
        experimentService.start();
    }

    @GetMapping(value = "/stop")
    public void stop() {
        experimentService.stop();
    }
}
