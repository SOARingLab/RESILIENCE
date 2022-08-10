package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @RequestMapping(value = "/risk-level")
    public Map<String, String> riskLevel(String region) {
        Map<String, String> result = new HashMap<>();
        if (region.equals("region_3")) {
            result.put("risk_level", "high");
        } else if (region.equals("region_2")) {
            result.put("risk_level", "medium");
        } else {
            result.put("risk_level", "low");
        }
        return result;
    }
}
