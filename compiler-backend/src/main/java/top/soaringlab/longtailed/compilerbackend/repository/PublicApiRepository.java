package top.soaringlab.longtailed.compilerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.compilerbackend.domain.PublicApi;

import java.util.List;

public interface PublicApiRepository extends JpaRepository<PublicApi, Long> {

    List<PublicApi> findByProcessId(String processId);
}
