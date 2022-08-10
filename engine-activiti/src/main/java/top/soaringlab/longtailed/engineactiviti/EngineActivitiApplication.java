package top.soaringlab.longtailed.engineactiviti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EngineActivitiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngineActivitiApplication.class, args);
	}

}
