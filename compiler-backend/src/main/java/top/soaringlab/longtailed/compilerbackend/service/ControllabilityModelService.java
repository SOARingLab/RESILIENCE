package top.soaringlab.longtailed.compilerbackend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;
import top.soaringlab.longtailed.compilerbackend.repository.ControllabilityModelRepository;

@Service
public class ControllabilityModelService {

    @Autowired
    private ControllabilityModelRepository controllabilityModelRepository;

    public List<ControllabilityModel> findByProcessId(String processId) {
        return controllabilityModelRepository.findByProcessId(processId);
    }

    public ControllabilityModel getOne(Long id) {
        return controllabilityModelRepository.getOne(id);
    }

    public ControllabilityModel save(ControllabilityModel controllabilityModel) {
        return controllabilityModelRepository.save(controllabilityModel);
    }

    public void delete(ControllabilityModel controllabilityModel) {
        controllabilityModelRepository.delete(controllabilityModel);
    }
}
