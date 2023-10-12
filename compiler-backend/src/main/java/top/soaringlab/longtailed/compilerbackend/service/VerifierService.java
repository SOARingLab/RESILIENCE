package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;
import top.soaringlab.longtailed.compilerbackend.dto.FunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.dto.NonFunctionalVerificationResult;
import top.soaringlab.longtailed.compilerbackend.verifier.NusmvVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.PrismVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.StnuHDCVerifier;
import top.soaringlab.longtailed.compilerbackend.verifier.StnuVerifier;

import java.util.List;

@Service
public class VerifierService {

    @Autowired
    private CompilerService compilerService;

    @Autowired
    private ProcessVariableService processVariableService;

    @Value("${prism-directory}")
    private String prismDirectoryName;

    @Value("${prism-program}")
    private String prismProgramName;

    // public Map<String, List<String>> verifyFunctional(String file, String start)
    // throws Exception {
    // FunctionalVerifier functionalVerifier = new FunctionalVerifier();
    // return functionalVerifier.functionalVerify(file, start);
    // }

    public FunctionalVerificationResult verifyFunctional(String processId, String file, String start) throws Exception {
        String fileAnnotation = compilerService.constraintRemoveAll(file);
        fileAnnotation = compilerService.compileForFunctional(processId, fileAnnotation);
        String fileFunctional = compilerService.constraintRemoveNonFunctional(file);

        NusmvVerifier nusmvVerifier = new NusmvVerifier();
        nusmvVerifier.processVariableList = processVariableService.findByProcessId(processId);
        return nusmvVerifier.functionalVerify(fileAnnotation, fileFunctional, start);
    }

    // public boolean verifyNonFunctional(String file) throws Exception {
    // NonFunctionalVerifier nonFunctionalVerifier = new NonFunctionalVerifier();
    // return nonFunctionalVerifier.nonFunctionalVerify(file);
    // }

    public NonFunctionalVerificationResult verifyNonFunctional(String file, String SLIs, String logic)
            throws Exception {
        StnuHDCVerifier stnuHDCVerifier = new StnuHDCVerifier();
        return stnuHDCVerifier.nonFunctionalVerify(file, SLIs, logic);
    }

    public ControllabilityModel buildControllability(ControllabilityModel controllabilityModel) throws Exception {
        PrismVerifier prismVerifier = new PrismVerifier();
        prismVerifier.prismDirectoryName = prismDirectoryName;
        prismVerifier.prismProgramName = prismProgramName;
        return prismVerifier.build(controllabilityModel);
    }

    public ControllabilityModel verifyControllability(ControllabilityModel controllabilityModel) throws Exception {
        PrismVerifier prismVerifier = new PrismVerifier();
        prismVerifier.prismDirectoryName = prismDirectoryName;
        prismVerifier.prismProgramName = prismProgramName;
        prismVerifier.build(controllabilityModel);
        return prismVerifier.verify(controllabilityModel);
    }

    public void verifyTest(String name) throws Exception {
        PrismVerifier prismVerifier = new PrismVerifier();
        prismVerifier.prismDirectoryName = prismDirectoryName;
        prismVerifier.prismProgramName = prismProgramName;
        prismVerifier.verifyTest(name);
    }
}
