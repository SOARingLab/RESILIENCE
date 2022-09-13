package top.soaringlab.longtailed.enginebackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.enginebackend.domain.ProcessLog;

import java.util.List;

public interface ProcessLogRepository extends JpaRepository<ProcessLog, Long> {

    List<ProcessLog> findByBusinessKey(String businessKey);

    Page<ProcessLog> findByOrderByIdDesc(Pageable pageable);
}
