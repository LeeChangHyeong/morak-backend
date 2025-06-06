package org.brokong.morakbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MorakBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MorakBackendApplication.class, args);
	}

}
