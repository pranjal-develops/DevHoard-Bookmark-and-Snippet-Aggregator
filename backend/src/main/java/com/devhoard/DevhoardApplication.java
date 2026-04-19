package com.devhoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan("com.devhoard")
public class DevhoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevhoardApplication.class, args);
	}

}
