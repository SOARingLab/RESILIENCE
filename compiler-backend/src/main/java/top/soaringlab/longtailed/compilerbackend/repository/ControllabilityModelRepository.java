package top.soaringlab.longtailed.compilerbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import top.soaringlab.longtailed.compilerbackend.domain.ControllabilityModel;

public interface ControllabilityModelRepository extends JpaRepository<ControllabilityModel, Long> {

    List<ControllabilityModel> findByProcessId(String processId);
}
