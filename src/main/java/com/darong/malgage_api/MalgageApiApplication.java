package com.darong.malgage_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 이거 추가!
public class MalgageApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MalgageApiApplication.class, args);
	}

}
