package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.dto.FunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.dto.NonFunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.verifier.NusmvVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.StnuHDCVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.StnuVerifier;

import java.util.List;

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

    public FunctionalVerificationResult verifyFunctional(String processId, String file, String start) throws Exception {
        String fileAnnotation = compilerService.constraintRemoveAll(file);
        fileAnnotation = compilerService.compileForFunctional(processId, fileAnnotation);
        String fileFunctional = compilerService.constraintRemoveNonFunctional(file);

        NusmvVerifier nusmvVerifier = new NusmvVerifier();
        nusmvVerifier.processVariableList = processVariableService.findByProcessId(processId);
        return nusmvVerifier.functionalVerify(fileAnnotation, fileFunctional, start);
    }

//    public boolean verifyNonFunctional(String file) throws Exception {
//        NonFunctionalVerifier nonFunctionalVerifier = new NonFunctionalVerifier();
//        return nonFunctionalVerifier.nonFunctionalVerify(file);
//    }

    public NonFunctionalVerificationResult verifyNonFunctional(String file, String SLIs) throws Exception {
        StnuHDCVerifier stnuHDCVerifier = new StnuHDCVerifier();
        return stnuHDCVerifier.nonFunctionalVerify(file,SLIs,"(T&&P)||P,P||T,T");
    }
}
