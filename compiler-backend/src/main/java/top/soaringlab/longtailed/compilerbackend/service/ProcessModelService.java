package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessModel;
import top.soaringlab.longtailed.compilerbackend.repository.ProcessModelRepository;

import java.util.List;

@Service
public class ProcessModelService {

    @Autowired
    private ProcessModelRepository processModelRepository;

    public List<ProcessModel> findByProcessId(String processId) {
        return processModelRepository.findByProcessId(processId);
    }

    public ProcessModel getOne(Long id) {
        return processModelRepository.getOne(id);
    }

    public ProcessModel save(ProcessModel processModel) {
        return processModelRepository.save(processModel);
    }

    public void delete(ProcessModel processModel) {
        processModelRepository.delete(processModel);
    }
}
