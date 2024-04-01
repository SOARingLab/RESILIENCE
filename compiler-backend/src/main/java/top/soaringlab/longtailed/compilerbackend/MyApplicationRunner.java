package top.soaringlab.longtailed.compilerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessActivity;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessModel;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;
import top.soaringlab.longtailed.compilerbackend.domain.Scenario;
import top.soaringlab.longtailed.compilerbackend.service.ControllabilityModelService;
import top.soaringlab.longtailed.compilerbackend.service.ProcessActivityService;
import top.soaringlab.longtailed.compilerbackend.service.ProcessModelService;
import top.soaringlab.longtailed.compilerbackend.service.ProcessVariableService;
import top.soaringlab.longtailed.compilerbackend.service.PublicApiService;
import top.soaringlab.longtailed.compilerbackend.service.ScenarioService;
import top.soaringlab.longtailed.compilerbackend.service.TestService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private ScenarioService scenarioService;

    @Autowired
    private ProcessModelService processModelService;

    @Autowired
    private ProcessActivityService processActivityService;

    @Autowired
    private ProcessVariableService processVariableService;

    @Autowired
    private PublicApiService publicApiService;

    @Autowired
    private ControllabilityModelService controllabilityModelService;

    @Autowired
    private TestService testService;

    @Value("${compiler-backend-url}")
    private String compilerBackendUrl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initScenario();
        initProcessModel();
        initProcessActivity();
        initProcessVariable();
        initPublicApi();
        initControllabilityModel();
        initTest();
    }

    private void initScenario() {
        Scenario scenario = new Scenario();
        scenario.setProcessIds(List.of(
                "online_grocery",
                "lng_logistics",
                "travel-agency",
                "handle-insurance-claim",
                "review-paper",
                "emergency-call",
                "outpatient",
                "set-up-project"));
        scenarioService.save(scenario);
    }

    private String readResourceFile(String filename) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(filename).getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private void initProcessModel() throws Exception {

        List<ProcessModel> processModelList = JSON.parseArray(readResourceFile("init/process-model.json"),
                ProcessModel.class);

        for (ProcessModel processModel : processModelList) {
            processModel.setData(readResourceFile("process-model/" + processModel.getFilename()));
            processModelService.save(processModel);
        }
    }

    private void initProcessActivity() throws Exception {

        List<ProcessActivity> processActivityList = JSON.parseArray(readResourceFile("init/process-activity.json"),
                ProcessActivity.class);

        for (ProcessActivity processActivity : processActivityList) {
            processActivityService.save(processActivity);
        }
    }

    private void initProcessVariable() throws Exception {

        List<ProcessVariable> processVariableList = JSON.parseArray(readResourceFile("init/process-variable.json"),
                ProcessVariable.class);

        for (ProcessVariable processVariable : processVariableList) {
            processVariableService.save(processVariable);
        }
    }

    private void initPublicApi() {

        // online grocery

        PublicApi publicApi = new PublicApi();
        publicApi.setProcessId("online_grocery");
        publicApi.setName("fake risk level of region");
        publicApi.setMethod("GET");
        publicApi.setUrl(compilerBackendUrl + "/fake-risk-level");
        publicApi.setInputFroms(List.of("region"));
        publicApi.setInputTos(List.of("region"));
        publicApi.setOutputFroms(List.of("fake_risk_level"));
        publicApi.setOutputTos(List.of("fake_risk_level"));
        publicApiService.save(publicApi);
    }

    private void initControllabilityModel() throws Exception {

        // online grocery

        ControllabilityModel controllabilityModel = new ControllabilityModel();
        controllabilityModel.setProcessId("online_grocery");
        controllabilityModel.setBpmnStart("Start");
        controllabilityModel.setBpmnModel(
                readResourceFile("controllability-model/online-grocery-bpmnModel.bpmn"));
        controllabilityModel.setBpmnVariableDefinitionList(
                readResourceFile("controllability-model/online-grocery-bpmnVariableDefinitionList.json"));
        controllabilityModel.setBpmnVariableConditionList(
                readResourceFile("controllability-model/online-grocery-bpmnVariableConditionList.json"));
        controllabilityModel.setBpmnVariableModificationList(
                readResourceFile("controllability-model/online-grocery-bpmnVariableModificationList.json"));
        controllabilityModel.setBpmnKpiList(
                readResourceFile("controllability-model/online-grocery-bpmnKpiList.json"));
        controllabilityModel.setVerificationModel(
                readResourceFile("controllability-model/online-grocery-verificationModel.prism"));
        controllabilityModel.setVerificationProperty(
                readResourceFile("controllability-model/online-grocery-verificationProperty.props"));
        controllabilityModel.setVerificationOutput(
                readResourceFile("controllability-model/online-grocery-verificationOutput.txt"));
        controllabilityModelService.save(controllabilityModel);
    }

    private void initTest() {
        testService.regionRiskLevel.put("DingHaiLuJieDao", "low");
        testService.regionRiskLevel.put("PingLiangLuJieDao", "low");
        testService.regionRiskLevel.put("JiangPuLuJieDao", "low");
        testService.regionRiskLevel.put("SiPingLuJieDao", "low");
        testService.regionRiskLevel.put("KongJiangLuJieDao", "medium");
        testService.regionRiskLevel.put("ChangBaiXinCunJieDao", "medium");
        testService.regionRiskLevel.put("YanJiXinCunJieDao", "medium");
        testService.regionRiskLevel.put("YinHangJieDao", "medium");
        testService.regionRiskLevel.put("DaQiaoJieDao", "high");
        testService.regionRiskLevel.put("WuJiaoChangJieDao", "high");
        testService.regionRiskLevel.put("XinJiangWanChengJieDao", "high");
        testService.regionRiskLevel.put("ChangHaiLuJieDao", "high");
    }
}
