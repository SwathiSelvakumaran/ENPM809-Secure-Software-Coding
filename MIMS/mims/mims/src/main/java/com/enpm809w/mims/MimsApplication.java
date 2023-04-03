package com.enpm809w.mims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MimsApplication {
	private static final Logger logger = LoggerFactory.getLogger(MimsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MimsApplication.class, args);
	}


}
