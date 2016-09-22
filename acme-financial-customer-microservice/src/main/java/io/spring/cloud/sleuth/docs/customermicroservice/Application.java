package io.spring.cloud.sleuth.docs.customermicroservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	@RequestMapping("/startOfCustomer-Microservice")
	public String customerMicroServiceController() throws InterruptedException {
		log.info("Hello from Acme's Customer Microservice");
		return "Hello from Acme's Customer Microservice";
	}

	@RequestMapping("/")
	public String frontPage() throws InterruptedException {
		log.info("Front Page");
		return "Front Page";
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
