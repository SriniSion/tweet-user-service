package com.tweetapp;

import java.util.Objects;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class UserServiceApplication {
	
	private final static String ENVIRONMENT = "FLIGHT_APP";

	public static void main(String[] args) {
		String env = System.getenv(ENVIRONMENT);
		new SpringApplicationBuilder().sources(UserServiceApplication.class)
				.profiles(Objects.isNull(env) || env.isEmpty() ? "local" : env).run(args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
