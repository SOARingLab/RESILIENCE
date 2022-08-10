package top.soaringlab.longtailed.compilerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.compilerbackend.domain.ProcessVariable;

import java.util.List;

public interface ProcessVariableRepository extends JpaRepository<ProcessVariable, Long> {

    List<ProcessVariable> findByProcessId(String processId);
}
