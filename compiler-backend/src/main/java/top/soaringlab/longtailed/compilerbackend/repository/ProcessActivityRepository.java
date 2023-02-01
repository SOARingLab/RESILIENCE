package top.soaringlab.longtailed.compilerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessActivity;

import java.util.List;

public interface ProcessActivityRepository extends JpaRepository<ProcessActivity, Long> {

    List<ProcessActivity> findByProcessId(String processId);
}
