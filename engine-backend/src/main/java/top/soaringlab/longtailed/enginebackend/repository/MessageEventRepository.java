package top.soaringlab.longtailed.enginebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.soaringlab.longtailed.enginebackend.domain.MessageEvent;

public interface MessageEventRepository extends JpaRepository<MessageEvent, Long> {
}
