package cn.udream.spring.eventdrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventdriveApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventdriveApplication.class, args);
	}

}
