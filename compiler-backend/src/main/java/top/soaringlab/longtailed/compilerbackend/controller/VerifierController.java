package top.soaringlab.longtailed.compilerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.soaringlab.longtailed.compilerbackend.dto.FunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.dto.NonFunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.service.VerifierService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/verifier")
public class VerifierController {

    @Autowired
    private VerifierService verifierService;

//    @PostMapping(value = "/verify-functional")
//    public Map<String, List<String>> verifyFunctional(@RequestBody Map<String, String> map) throws Exception {
//        return verifierService.verifyFunctional(map.get("file"), map.get("start"));
//    }

    @PostMapping(value = "/verify-functional")
    public FunctionalVerificationResult verifyFunctional(@RequestBody Map<String, String> map) throws Exception {
        return verifierService.verifyFunctional(map.get("processId"), map.get("file"), map.get("start"));
    }

//    @PostMapping(value = "/verify-non-functional")
//    public Boolean verifyNonFunctional(@RequestBody String file) throws Exception {
//        return verifierService.verifyNonFunctional(file);
//    }

    @PostMapping(value = "/verify-non-functional")
    public NonFunctionalVerificationResult verifyNonFunctional(@RequestBody Map<String, String> map) throws Exception {
        return verifierService.verifyNonFunctional(map.get("file"), map.get("SLIs"));
    }
}
