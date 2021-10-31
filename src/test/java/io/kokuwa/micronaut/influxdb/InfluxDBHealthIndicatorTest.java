package io.kokuwa.micronaut.influxdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.domain.HealthCheck;

import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.health.HealthStatus;
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
			var endpoint = context.getBean(InfluxDBHealthIndicator.class);
			var result = Mono.from(endpoint.getResult()).block();
			assertEquals("influxdb", result.getName(), "name");
			assertEquals(HealthStatus.UP, result.getStatus(), "status");
			assertTrue(result.getDetails() instanceof HealthCheck, "details");
			assertTrue(((HealthCheck) result.getDetails()).getMessage().startsWith("ready for queries"), "message");
		});
	}

	@DisplayName("healh: down because of connection")
	@Test
	void downConnection() {
		run(Map.of("influxdb.url", "http://localhost:" + SocketUtils.findAvailableTcpPort()), context -> {
			var endpoint = context.getBean(InfluxDBHealthIndicator.class);
			var result = Mono.from(endpoint.getResult()).block();
			assertEquals("influxdb", result.getName(), "name");
			assertEquals(HealthStatus.DOWN, result.getStatus(), "status");
			assertTrue(result.getDetails() instanceof HealthCheck, "details");
			assertTrue(((HealthCheck) result.getDetails()).getMessage().startsWith("Failed to connect to "), "message");
		});
	}
}
