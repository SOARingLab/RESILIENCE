package top.soaringlab.longtailed.compilerbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessModel;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;
import top.soaringlab.longtailed.compilerbackend.service.ProcessModelService;
import top.soaringlab.longtailed.compilerbackend.service.ProcessVariableService;
import top.soaringlab.longtailed.compilerbackend.service.PublicApiService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private ProcessModelService processModelService;

    @Autowired
    private ProcessVariableService processVariableService;

    @Autowired
    private PublicApiService publicApiService;

    @Value("${compiler-backend-url}")
    private String compilerBackendUrl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initProcessModel();
        initProcessVariable();
        initPublicApi();
    }

    private void initProcessModel() throws Exception {
        ProcessModel processModel = new ProcessModel();
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
        processModel.setFilename("online-grocery-functional-detail.bpmn");
        processModel.setData(readResourceFile("process-model/online-grocery-functional-detail.bpmn"));
        processModelService.save(processModel);
    }

    private String readResourceFile(String filename) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ClassPathResource(filename).getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private void initProcessVariable() {
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
        processVariable.setName("risk_level");
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
    }

    private void initPublicApi() {
        PublicApi publicApi = new PublicApi();
        publicApi.setProcessId("online_grocery");
        publicApi.setName("risk level of region");
        publicApi.setMethod("GET");
        publicApi.setUrl(compilerBackendUrl + "/risk-level");
        publicApi.setInputFroms(List.of("region"));
        publicApi.setInputTos(List.of("region"));
        publicApi.setOutputFroms(List.of("risk_level"));
        publicApi.setOutputTos(List.of("risk_level"));
        publicApiService.save(publicApi);
    }
}
