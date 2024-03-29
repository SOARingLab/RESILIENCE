package top.soaringlab.longtailed.compilerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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
        scenario.setProcessIds(List.of("online_grocery", "lng_logistics"));
        scenarioService.save(scenario);
    }

    private void initProcessModel() throws Exception {

        // online grocery

        ProcessModel processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-customer.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-customer.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-shop.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-shop.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-annotation.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-annotation.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-constraint.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-constraint.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-customer-jbpm.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-customer-jbpm.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-shop-jbpm.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-shop-jbpm.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("online_grocery");
        processModel.setFilename("online-grocery-controllability.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-controllability.bpmn"));
        processModelService.save(processModel);

        // lng logistics

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency-annotation.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency-annotation.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency-constraint.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency-constraint.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency-instance-1.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency-instance-1.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency-instance-2.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency-instance-2.bpmn"));
        processModelService.save(processModel);

        processModel = new ProcessModel();
        processModel.setProcessId("lng_logistics");
        processModel.setFilename("lng-logistics-emergency-instance-3.bpmn");
        processModel.setData(readResourceFile("process-model/lng-logistics-emergency-instance-3.bpmn"));
        processModelService.save(processModel);
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

    private void initProcessActivity() {

        // online grocery

        ProcessActivity processActivity = new ProcessActivity();
        processActivity.setProcessId("online_grocery");
        processActivity.setName("Process inventory");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("online_grocery");
        processActivity.setName("Deliver goods");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("online_grocery");
        processActivity.setName("Confirm delivery");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("online_grocery");
        processActivity.setName("Process payment");
        processActivityService.save(processActivity);

        // lng logistics

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Disconnect pipes and leave dock");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Change course");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Contact relevant bureaus");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Notify nearby ships");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Give first aid");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Contact first-aid center");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Ventilate");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Close doors and block vents");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Disperse gas");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Put out fire");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Plug leak");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Discharge water");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Spray water curtain");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Transfer goods");
        processActivityService.save(processActivity);

        processActivity = new ProcessActivity();
        processActivity.setProcessId("lng_logistics");
        processActivity.setName("Eject goods");
        processActivityService.save(processActivity);
    }

    private void initProcessVariable() {

        // online grocery

        ProcessVariable processVariable = new ProcessVariable();
        processVariable.setProcessId("online_grocery");
        processVariable.setName("order_status");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"pending\"");
        processVariable.setValueRange(List.of("\"pending\"", "\"finished\"", "\"canceled\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("online_grocery");
        processVariable.setName("region");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"region_1\"", "\"region_2\"", "\"region_3\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("online_grocery");
        processVariable.setName("fake_risk_level");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"low\"", "\"medium\"", "\"high\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("online_grocery");
        processVariable.setName("delivery_method");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"home_delivery\"", "\"contactless_locker\"", "\"at_store\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("online_grocery");
        processVariable.setName("amount");
        processVariable.setType("Number");
        processVariable.setDefaultValue("0");
        processVariable.setMinimumValue("0");
        processVariable.setMaximumValue("1000");
        processVariableService.save(processVariable);

        // lng logistics

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("action");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"plug_leak\"");
        processVariable.setValueRange(List.of("\"move_tanker\"", "\"rescue_the_injured\"", "\"plug_leak\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("tanker_location");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"at_sea\"");
        processVariable.setValueRange(List.of("\"in_dock\"", "\"at_sea\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("need_contact");
        processVariable.setType("Boolean");
        processVariable.setDefaultValue("true");
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("injury");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"frostbite\"");
        processVariable.setValueRange(List.of("\"frostbite\"", "\"asphyxia\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("need_ambulance");
        processVariable.setType("Boolean");
        processVariable.setDefaultValue("true");
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("area_opening");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"open\"");
        processVariable.setValueRange(List.of("\"open\"", "\"close\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("danger_control");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"leak\"");
        processVariable.setValueRange(List.of("\"leak\"", "\"fire\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("leak_type");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"pipe\"");
        processVariable.setValueRange(List.of("\"pipe\"", "\"casing\"", "\"massive_leak\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("goods_disposal");
        processVariable.setType("String");
        processVariable.setDefaultValue("\"retain\"");
        processVariable.setValueRange(List.of("\"retain\"", "\"transfer\"", "\"eject\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("leak_location");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"above_water\"", "\"under_water\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("leak_amount");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"little\"", "\"medium\"", "\"massive\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("catch_fire");
        processVariable.setType("Boolean");
        processVariable.setDefaultValue("random");
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("tanker_condition");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"stable\"", "\"unstable\""));
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("ship_nearby");
        processVariable.setType("Boolean");
        processVariable.setDefaultValue("random");
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("fishery_nearby");
        processVariable.setType("Boolean");
        processVariable.setDefaultValue("random");
        processVariableService.save(processVariable);

        processVariable = new ProcessVariable();
        processVariable.setProcessId("lng_logistics");
        processVariable.setName("wind_direction");
        processVariable.setType("String");
        processVariable.setDefaultValue("random");
        processVariable.setValueRange(List.of("\"windward\"", "\"leeward\""));
        processVariableService.save(processVariable);
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
