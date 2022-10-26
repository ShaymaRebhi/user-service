package com.proxym.poleactualities;

import com.proxym.poleactualities.Models.User;
import com.proxym.poleactualities.Repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;
import java.util.stream.Stream;

@EnableSwagger2
@EnableEurekaClient
@SpringBootApplication
public class PoleActualitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PoleActualitiesApplication.class, args);
	}
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Bean
	ApplicationRunner init(UserRepository repository) {
		return args -> {
			Stream.of("shayma", "asma", "oumayma" , "ons").forEach(username -> {
				repository.save(new User(username));
			});
			repository.findAll().forEach(System.out::println);
		};
	}
}
