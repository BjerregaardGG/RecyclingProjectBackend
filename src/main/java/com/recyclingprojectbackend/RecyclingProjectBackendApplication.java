package com.recyclingprojectbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RecyclingProjectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecyclingProjectBackendApplication.class, args);
	}

}
