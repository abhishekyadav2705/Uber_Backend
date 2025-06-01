package com.backend.abhishek.uber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UberBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberBackendApplication.class, args);
	}

}
