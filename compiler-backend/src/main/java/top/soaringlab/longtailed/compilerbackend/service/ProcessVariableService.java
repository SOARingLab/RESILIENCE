package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;
import top.soaringlab.longtailed.compilerbackend.repository.ProcessVariableRepository;

import java.util.List;

@Service
public class ProcessVariableService {

    @Autowired
    private ProcessVariableRepository processVariableRepository;

    public List<ProcessVariable> findByProcessId(String processId) {
        return processVariableRepository.findByProcessId(processId);
    }

    public ProcessVariable getOne(Long id) {
        return processVariableRepository.getOne(id);
    }

    public ProcessVariable save(ProcessVariable processVariable) {
        return processVariableRepository.save(processVariable);
    }

    public void delete(ProcessVariable processVariable) {
        processVariableRepository.delete(processVariable);
    }
}
