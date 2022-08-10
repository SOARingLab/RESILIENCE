package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.compiler.Compiler;
import top.soaringlab.longtailed.compilerbackend.compiler.CompilerScript;
import top.soaringlab.longtailed.compilerbackend.compiler.ConstraintRemover;

@Service
public class CompilerService {

    @Autowired
    private PublicApiService publicApiService;

    public String constraintRemoveAll(String file) throws Exception {
        ConstraintRemover constraintRemover = new ConstraintRemover();
        return constraintRemover.constraintRemoveAll(file);
    }

    public String constraintRemoveFunctional(String file) throws Exception {
        ConstraintRemover constraintRemover = new ConstraintRemover();
        return constraintRemover.constraintRemoveFunctional(file);
    }

    public String constraintRemoveNonFunctional(String file) throws Exception {
        ConstraintRemover constraintRemover = new ConstraintRemover();
        return constraintRemover.constraintRemoveNonFunctional(file);
    }

    public String compile(String processId, String file) throws Exception {
        Compiler compiler = new Compiler();

        // jbpm
        // compiler.processEngine = "jbpm";

        compiler.publicApiList = publicApiService.findByProcessId(processId);
        return compiler.compile(file);
    }

    public String compileForFunctional(String processId, String file) throws Exception {
        Compiler compiler = new Compiler();
        compiler.processEngine = "functional";
        return compiler.compile(file);
    }

    public String compileScript(String text) throws Exception {
        CompilerScript compilerScript = new CompilerScript();
        return compilerScript.compileScript(text);
    }
}
