/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.cloud.samples.brewery.acceptance

import groovy.util.logging.Slf4j
import io.spring.cloud.samples.brewery.acceptance.common.tech.ExceptionLoggingRestTemplate
import io.spring.cloud.samples.brewery.acceptance.common.tech.TestConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static com.jayway.awaitility.Awaitility.await
import static java.util.concurrent.TimeUnit.SECONDS

@ContextConfiguration(classes = TestConfiguration, loader = SpringApplicationContextLoader)
@Slf4j
class MessageFlowSpec extends Specification {

	@Value('${service.url:http://localhost:8081}') String service1Url
	@Value('${zipkin.query.port:9411}') Integer zipkinQueryPort
	@Value('${LOCAL_URL:http://localhost}') String zipkinQueryUrl

	def 'should send message to service1 and receive combined response'() {
		expect:
			await().pollInterval(1, SECONDS).atMost(60, SECONDS).until(new Runnable() {
				@Override
				void run() {
					URI uri = URI.create("$service1Url/start")
					log.info("Sending request to service1Response [$uri]")
					ResponseEntity<String> service1Response = restTemplate().exchange(
							new RequestEntity<>(new HttpHeaders(), HttpMethod.GET, uri), String
					)
					log.info("Response from service1Response is [$service1Response]")
					assert service1Response.statusCode == HttpStatus.OK
					assert service1Response.body == 'Got response from service2 [Hello from service2, response from service3 [Hello from service3] and from service4 [Hello from service4]]'
					log.info("The Sleuth Docs apps are working! Let's be happy!")
				}
			})
	}


	RestTemplate restTemplate() {
		return new ExceptionLoggingRestTemplate()
	}
}
