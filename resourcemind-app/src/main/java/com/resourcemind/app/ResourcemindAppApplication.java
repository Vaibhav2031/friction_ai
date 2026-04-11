package com.resourcemind.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ResourcemindAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourcemindAppApplication.class, args);
	}

}
