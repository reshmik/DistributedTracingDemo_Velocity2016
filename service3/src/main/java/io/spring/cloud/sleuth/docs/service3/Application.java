package io.spring.cloud.sleuth.docs.service3;

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

	@RequestMapping("/bar")
	public String start() throws InterruptedException {
		Thread.sleep(300);
		log.info("Hello from service3");
		return "Hello from service3";
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
