package top.soaringlab.longtailed.compilerbackend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.soaringlab.longtailed.compilerbackend.domain.Scenario;
import top.soaringlab.longtailed.compilerbackend.repository.ScenarioRepository;

@Service
public class ScenarioService {

    @Autowired
    private ScenarioRepository scenarioRepository;

    public List<Scenario> findAll() {
        return scenarioRepository.findAll();
    }

    public Scenario getOne(Long id) {
        return scenarioRepository.getOne(id);
    }

    public Scenario save(Scenario scenario) {
        return scenarioRepository.save(scenario);
    }

    public void delete(Scenario scenario) {
        delete(scenario);
    }
}
