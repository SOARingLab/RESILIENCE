package top.soaringlab.longtailed.enginejbpm.service;

import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.enginejbpm.GroovyScript;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class DeployService {

    @Autowired
    private GroovyScript groovyScript;

    private KieHelper kieHelper = new KieHelper();

    private KieBase kieBase;

    public void create(String filename, String file) {
        Resource processModel = ResourceFactory.newByteArrayResource(file.getBytes(StandardCharsets.UTF_8));
        processModel.setSourcePath(filename);

        kieBase = kieHelper
                .addResource(processModel)
                .build();
    }

    public void start(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        variables.put("S", groovyScript);
        KieSession ksession = kieBase.newKieSession();
        ProcessInstance processInstance = ksession.startProcess(processDefinitionKey, variables);
    }
}
