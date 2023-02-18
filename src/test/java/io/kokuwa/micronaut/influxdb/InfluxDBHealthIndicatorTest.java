package io.kokuwa.micronaut.influxdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.domain.Ready;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.health.HealthStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.management.health.indicator.HealthResult;
import io.micronaut.runtime.server.EmbeddedServer;
import reactor.core.publisher.Mono;

/**
 * Test for {@link InfluxDBHealthIndicator}.
 *
 * @author Stephan Schnabel
 */
public class InfluxDBHealthIndicatorTest extends AbstractTest {

	@DisplayName("healh: up")
	@Test
	void up() {
		run(Map.of(), context -> {

			var health = Mono.from(context.getBean(InfluxDBHealthIndicator.class).getResult()).block();
			assertEquals("influxdb", health.getName(), "name");
			assertEquals(HealthStatus.UP, health.getStatus(), "status");
			assertTrue(health.getDetails() instanceof Ready, "details");

			health = health(context, HttpStatus.OK);
			assertEquals(HealthStatus.UP, health.getStatus(), "status");
			var influxDetails = ((Map<String, Map<String, String>>) health.getDetails()).get("influxdb");
			assertNotNull(influxDetails, "details for influx on /health missing");
			assertEquals(HealthStatus.UP.toString(), influxDetails.get("status"), "status on /health invalid");
			assertNotNull(influxDetails.get("details"), "details on /health invalid");
		});
	}

	@DisplayName("healh: down because of connection")
	@Test
	void down() {
		run(Map.of("influxdb.url", "http://localhost:" + SocketUtils.findAvailableTcpPort()), context -> {

			var health = Mono.from(context.getBean(InfluxDBHealthIndicator.class).getResult()).block();
			assertEquals("influxdb", health.getName(), "name");
			assertEquals(HealthStatus.DOWN, health.getStatus(), "status");
			assertNull(health.getDetails(), "details");

			health = health(context, HttpStatus.SERVICE_UNAVAILABLE);
			assertEquals(HealthStatus.DOWN, health.getStatus(), "status");
			var influxDetails = ((Map<String, Map<String, String>>) health.getDetails()).get("influxdb");
			assertNotNull(influxDetails, "details for influx on /health missing");
			assertEquals(HealthStatus.DOWN.toString(), influxDetails.get("status"), "status on /health invalid");
			assertNull(influxDetails.get("details"), "details on /health invalid");
		});
	}

	private HealthResult health(ApplicationContext context, HttpStatus status) {

		var server = context.getBean(EmbeddedServer.class).start();
		var client = context.getBean(HttpClient.class).toBlocking();
		var request = HttpRequest.GET("http://localhost:" + server.getPort() + "/health");

		HttpResponse<HealthResult> response;
		try {
			response = client.exchange(request, HealthResult.class);
		} catch (HttpClientResponseException e) {
			response = (HttpResponse<HealthResult>) e.getResponse();
		}

		assertEquals(status, response.status(), "status");
		var body = response.getBody(HealthResult.class);
		assertTrue(body.isPresent(), "body missing");
		return body.get();
	}
}
