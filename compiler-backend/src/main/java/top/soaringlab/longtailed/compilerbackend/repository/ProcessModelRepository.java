package top.soaringlab.longtailed.compilerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessModel;

import java.util.List;

public interface ProcessModelRepository extends JpaRepository<ProcessModel, Long> {

    List<ProcessModel> findByProcessId(String processId);
}
