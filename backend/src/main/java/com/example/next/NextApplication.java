package com.example.next;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NextApplication {

	public static void main(String[] args) {
		SpringApplication.run(NextApplication.class, args);
	}

}
