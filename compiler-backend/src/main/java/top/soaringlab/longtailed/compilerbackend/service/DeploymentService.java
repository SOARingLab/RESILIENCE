package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
public class DeploymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CompilerService compilerService;

    @Value("${engine-backend-url}")
    private String engineBackendUrl;

    public void deploy(String processId, String filename, String data) throws Exception {
        data = compilerService.constraintRemoveAll(data);
        deployToProcessEngine(filename, data);
    }

    public void deployWithAnnotations(String processId, String filename, String data) throws Exception {
        data = compilerService.constraintRemoveAll(data);
        data = compilerService.compile(processId, data);
        deployToProcessEngine(filename, data);
    }

    private void deployToProcessEngine(String filename, String data) {

        ByteArrayResource byteArrayResource = new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("data", byteArrayResource);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);

        String url = engineBackendUrl + "/api/deployment/create";
        restTemplate.postForEntity(url, httpEntity, Object.class);

        // Camunda
        // restTemplate.postForEntity("http://localhost:8082/api/deployment/create", httpEntity, Object.class);

        // Activiti
        // restTemplate.postForEntity("http://localhost:8094/api/deployment/create", httpEntity, Object.class);

        // jbpm
        // restTemplate.postForEntity("http://localhost:8096/api/deployment/create", httpEntity, Object.class);
    }
}
