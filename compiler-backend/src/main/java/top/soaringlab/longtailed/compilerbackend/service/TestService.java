package top.soaringlab.longtailed.compilerbackend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public Map<String, String> regionRiskLevel = new HashMap<>();

    public String getRiskLevel(String region) {
        if (regionRiskLevel.containsKey(region)) {
            return regionRiskLevel.get(region);
        } else {
            return "low";
        }
    }
}
