package com.devhoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DevhoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevhoardApplication.class, args);
	}

}
