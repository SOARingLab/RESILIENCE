package top.soaringlab.longtailed.compilerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import top.soaringlab.longtailed.compilerbackend.domain.Scenario;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
