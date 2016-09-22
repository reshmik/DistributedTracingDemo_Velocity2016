package io.spring.cloud.sleuth.docs.backofficemicroservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired RestTemplate restTemplate;
	@Autowired Tracer tracer;


	@Value("${accountmicroservice.address:localhost:8083}") String accountMicroserviceAddress;
	@Value("${customermicroService.address:localhost:8084}") String customerMicroServiceAddress;
	private static final int MOCK_PORT = 8765;

	WireMock wireMock = new WireMock(MOCK_PORT);
	WireMockServer wireMockServer = new WireMockServer(MOCK_PORT);

	@PostConstruct
	public void setup() {
		wireMockServer.start();
		wireMock.register(any(urlMatching(".*")).willReturn(aResponse().withFixedDelay(3000)));
	}

	@PreDestroy
	public void shutdown() {
		wireMock.shutdown();
		wireMockServer.shutdown();
	}

	@RequestMapping("/")
	public String frontPage() throws InterruptedException {
		log.info("Front Page");
		return "Front Page";
	}

	@RequestMapping("/startOfBackOffice-Service")
	public String backOfficeMicroServiceController() throws InterruptedException {
		log.info("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Microservice and then Customer Microservice");
		String accountMicroservice = restTemplate.getForObject("http://" + accountMicroserviceAddress + "/startOfAccount-Microservice", String.class);
		log.info("Got response from Acme Financial's Account Service [{}]", accountMicroservice);
		String customerMicroService = restTemplate.getForObject("http://" + customerMicroServiceAddress + "/startOfCustomer-Microservice", String.class);
		log.info("Got response from Acme Financial's Customer Service [{}]", customerMicroService);
		return String.format("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Service [%s] and then Customer Service [%s]", accountMicroservice, customerMicroService);
	}

	@RequestMapping("/readtimeout")
	public String connectionTimeout() throws InterruptedException {
		Span span = this.tracer.createSpan("second_span");
		Thread.sleep(500);
		try {
			log.info("Calling a missing service");
			restTemplate.getForObject("http://localhost:" + MOCK_PORT + "/readtimeout", String.class);
			return "Should blow up";
		} finally {
			this.tracer.close(span);
		}
	}

	@Bean
	RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(2000);
		clientHttpRequestFactory.setReadTimeout(3000);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override public boolean hasError(ClientHttpResponse response)
					throws IOException {
				try {
					return super.hasError(response);
				} catch (Exception e) {
					return true;
				}
			}

			@Override public void handleError(ClientHttpResponse response)
					throws IOException {
				try {
					super.handleError(response);
				} catch (Exception e) {
					log.error("Exception [" + e.getMessage() + "] occurred while trying to send the request", e);
					throw e;
				}
			}
		});
		return restTemplate;
	}

	public static void main(String... args) {
		new SpringApplication(Application.class).run(args);
	}
}
