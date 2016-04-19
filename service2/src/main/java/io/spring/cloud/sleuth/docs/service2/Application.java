package io.spring.cloud.sleuth.docs.service2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired RestTemplate restTemplate;
	@Value("${service3.address:localhost:8083}") String serviceAddress3;
	@Value("${service4.address:localhost:8084}") String serviceAddress4;

	@RequestMapping("/foo")
	public String start() throws InterruptedException {
		Thread.sleep(200);
		log.info("Hello from service2. Calling service3 and then service4");
		String service3 = restTemplate.getForObject("http://" + serviceAddress3 + "/bar", String.class);
		log.info("Got response from service3 [{}]", service3);
		String service4 = restTemplate.getForObject("http://" + serviceAddress4 + "/baz", String.class);
		log.info("Got response from service4 [{}]", service4);
		return String.format("Hello from service2, response from service3 [%s] and from service4 [%s]", service3, service4);
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
