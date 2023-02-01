package top.soaringlab.longtailed.compilerbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessActivity;
import top.soaringlab.longtailed.compilerbackend.repository.ProcessActivityRepository;

import java.util.List;

@Service
public class ProcessActivityService {

    @Autowired
    private ProcessActivityRepository processActivityRepository;

    public List<ProcessActivity> findByProcessId(String processId) {
        return processActivityRepository.findByProcessId(processId);
    }

    public ProcessActivity getOne(Long id) {
        return processActivityRepository.getOne(id);
    }

    public ProcessActivity save(ProcessActivity processActivity) {
        return processActivityRepository.save(processActivity);
    }

    public void delete(ProcessActivity processActivity) {
        processActivityRepository.delete(processActivity);
    }
}
