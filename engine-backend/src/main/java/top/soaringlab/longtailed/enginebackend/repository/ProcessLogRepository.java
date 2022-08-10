package top.soaringlab.longtailed.enginebackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.enginebackend.domain.ProcessLog;

public interface ProcessLogRepository extends JpaRepository<ProcessLog, Long> {

    Page<ProcessLog> findByBusinessKey(String businessKey, Pageable pageable);

    Page<ProcessLog> findByOrderByIdDesc(Pageable pageable);
}
