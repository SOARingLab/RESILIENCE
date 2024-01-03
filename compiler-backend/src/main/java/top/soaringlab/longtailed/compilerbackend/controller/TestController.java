package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.soaringlab.longtailed.compilerbackend.service.TestService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping(value = "/fake-risk-level")
    public Map<String, String> getRiskLevel(String region) {
        Map<String, String> result = new HashMap<>();
        result.put("fake_risk_level", testService.getRiskLevel(region));
        return result;
    }
}
