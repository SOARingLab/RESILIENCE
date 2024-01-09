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

import top.soaringlab.longtailed.compilerbackend.domain.Scenario;
import top.soaringlab.longtailed.compilerbackend.service.ScenarioService;

@RestController
@RequestMapping(value = "/api/scenario")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;

    @GetMapping(value = "/find-all")
    public List<Scenario> findAll() {
        return scenarioService.findAll();
    }

    @GetMapping(value = "/get-one/{scenario}")
    public Scenario getOne(@PathVariable Scenario scenario) {
        return scenario;
    }

    @PostMapping(value = "/save")
    public Scenario save(@RequestBody Scenario scenario) {
        return scenarioService.save(scenario);
    }

    @DeleteMapping(value = "/delete/{scenario}")
    public void delete(@PathVariable Scenario scenario) {
        scenarioService.delete(scenario);
    }
}
