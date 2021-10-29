package io.kokuwa.micronaut.influxdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.HealthCheck.StatusEnum;

/**
 * Test for {@link InfluxDBClientFactory}.
 *
 * @author Stephan Schnabel
 */
public class InfluxDBFactoryTest extends AbstractTest {

	@DisplayName("getClient")
	@Test
	void getClient() {
		run(Map.of(), context -> {
			var client = context.getBean(InfluxDBClient.class);
			assertNotNull(client.ready().getStatus(), "client ready");
			assertEquals(StatusEnum.PASS, client.health().getStatus(), "client health");
		});
	}
}
