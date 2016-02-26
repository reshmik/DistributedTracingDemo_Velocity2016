package io.spring.cloud.sleuth.docs.service1;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired RestTemplate restTemplate;

	@RequestMapping("/start")
	public String start() {
		log.info("Hello from service1. Calling service2");
		String response = restTemplate.getForObject("http://localhost:8082/foo",String.class);
		log.info("Got response from service2 [{}]", response);
		return response;
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
