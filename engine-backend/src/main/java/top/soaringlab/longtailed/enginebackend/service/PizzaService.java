package top.soaringlab.longtailed.enginebackend.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.soaringlab.longtailed.enginebackend.GroovyScript;

import java.util.List;

@Service
public class PizzaService {

    static class QueryEpidemicResponse {

        static class Result {

            private Integer currentConfirmedCount;

            public Integer getCurrentConfirmedCount() {
                return currentConfirmedCount;
            }

            public void setCurrentConfirmedCount(Integer currentConfirmedCount) {
                this.currentConfirmedCount = currentConfirmedCount;
            }
        }

        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    public int queryEpidemic(String area) {
        QueryEpidemicResponse response = restTemplate.getForObject("https://lab.isaaclin.cn/nCoV/api/area?provinceEng={area}", QueryEpidemicResponse.class, area);
        assert response != null;
        return response.results.get(0).currentConfirmedCount;
    }

    public boolean isHighRisk(String area) {
        return queryEpidemic(area) >= 100;
    }

    public boolean isMediumRisk(String area) {
        return queryEpidemic(area) >= 50;
    }

    public void makeFreePizza() {
//        GroovyScript.log("Make free pizza");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.startProcessInstanceByMessage("Order received", "FreePizza");
    }
}
