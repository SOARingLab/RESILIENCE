package top.soaringlab.longtailed.enginebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.soaringlab.longtailed.enginebackend.domain.ProcessLog;
import top.soaringlab.longtailed.enginebackend.repository.ProcessLogRepository;

import java.util.List;

@Service
@Transactional
public class ProcessLogService {

    @Autowired
    private ProcessLogRepository processLogRepository;

    public Page<ProcessLog> findAll(int page, int size) {
        return processLogRepository.findAll(PageRequest.of(page, size));
    }

    public List<ProcessLog> findByBusinessKey(String businessKey) {
        return processLogRepository.findByBusinessKey(businessKey);
    }

    public String findLatest() {
        Page<ProcessLog> processLogPage = processLogRepository.findByOrderByIdDesc(PageRequest.of(0, 20));
        List<ProcessLog> processLogList = processLogPage.getContent();
        StringBuilder stringBuilder = new StringBuilder();
        for (ProcessLog processLog : processLogList) {
            stringBuilder.append(processLog.getMessage()).append("\n");
        }
        return stringBuilder.toString();
    }

    public void log(String businessKey, String message) {
        processLogRepository.save(new ProcessLog(businessKey, message));
    }
}
