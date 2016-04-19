package io.spring.cloud.sleuth.docs.service4;

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

	@RequestMapping("/baz")
	public String start() throws InterruptedException {
		Thread.sleep(400);
		log.info("Hello from service4");
		return "Hello from service4";
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
