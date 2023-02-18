package io.kokuwa.micronaut.influxdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

/**
 * Test for {@link InfluxDBClientFactory}.
 *
 * @author Stephan Schnabel
 */
public class InfluxDBFactoryTest extends AbstractTest {

	@DisplayName("client")
	@Test
	void client() {
		run(Map.of(), context -> {
			var client = context.getBean(InfluxDBClient.class);
			assertNotNull(client.ready().getStatus(), "client ready");
			assertTrue(client.ping(), "ping failed");
			assertEquals("v2.6.1", client.version(), "version failed");
		});
	}
}
