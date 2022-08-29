package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.verifier.NusmvVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.StnuVerifier;

@Service
public class VerifierService {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private ProcessVariableService processVariableService;

//    public Map<String, List<String>> verifyFunctional(String file, String start) throws Exception {
//        FunctionalVerifier functionalVerifier = new FunctionalVerifier();
//        return functionalVerifier.functionalVerify(file, start);
//    }

    public boolean verifyFunctional(String processId, String file, String start) throws Exception {
        file = compilerService.constraintRemoveNonFunctional(file);
        file = compilerService.compileForFunctional(processId, file);
        NusmvVerifier nusmvVerifier = new NusmvVerifier();
        nusmvVerifier.processVariableList = processVariableService.findByProcessId(processId);
        return nusmvVerifier.functionalVerify(file, start);
    }

//    public boolean verifyNonFunctional(String file) throws Exception {
//        NonFunctionalVerifier nonFunctionalVerifier = new NonFunctionalVerifier();
//        return nonFunctionalVerifier.nonFunctionalVerify(file);
//    }

    public boolean verifyNonFunctional(String file) throws Exception {
        StnuVerifier stnuVerifier = new StnuVerifier();
        return stnuVerifier.nonFunctionalVerify(file);
    }
}
