package io.spring.cloud.sleuth.docs.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import java.lang.invoke.MethodHandles;

@SpringBootApplication
@RestController
@SuppressWarnings("Duplications")
public class Application {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired RestTemplate restTemplate;
	@Autowired Tracer tracer;
	@Value("${backofficemicroservice.address:localhost:8082}") String backOfficeMicroServiceAddress;


	@RequestMapping("/")
	public String frontPage() throws InterruptedException {
		log.info("Front Page");
		return "Front Page";
	}

	@RequestMapping("/velocitystart")
	public String velocitystart() throws InterruptedException {
		Span span = null;
		try {
			log.info("Welcome To Acme Financial. Calling Acme Financial's Back Office Microservice");
			span = tracer.createSpan("callingBackOfficeMicroService_span");
			span.logEvent("call_backOfficeMicroService");
			span.logEvent("start_sleep_event");
			Thread.sleep(2000);
			span.logEvent("end_sleep_event");
			String response = restTemplate.getForObject("http://" + backOfficeMicroServiceAddress + "/startOfBackOffice-Service", String.class);
			span.logEvent("response_received_backOfficeMicroService");
			tracer.addTag("ui","success");
			log.info("Got response from Acme Financial's Back Office Microservice [{}]", response);
			return response;
		}finally {
			tracer.close(span);
		}
	}


	@RequestMapping("/springonestart")
	public String springonestart() throws InterruptedException {

		Span span = null;
		try {
			log.error("something went wrong");
			span = tracer.createSpan("call_backofficemicroservice");
			String response = null;
			if(response == null) {
				span.logEvent("call_backofficemicroservice_failed");
				tracer.addTag("ui","error");
			}
			return "wrong conference, please check reshmi's sanity!!!!";
		} finally {
			tracer.close(span);
		}

	}

	@RequestMapping("/readtimeout")
	public String timeout() throws InterruptedException {
		Span span = this.tracer.createSpan("first_span");
		try {
			Thread.sleep(300);
			log.info("Hello from Acme Financial UI. Calling backOfficeMicroService - should end up with read timeout");
			String response = restTemplate.getForObject("http://" + backOfficeMicroServiceAddress + "/readtimeout", String.class);
			log.info("Got response from backOfficeMicroService [{}]", response);
			return response;
		} finally {
			this.tracer.close(span);
		}
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
