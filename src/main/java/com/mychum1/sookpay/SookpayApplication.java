package com.mychum1.sookpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SookpayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SookpayApplication.class, args);
	}

}
