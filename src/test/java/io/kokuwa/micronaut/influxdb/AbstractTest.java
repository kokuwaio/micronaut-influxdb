package io.kokuwa.micronaut.influxdb;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.micronaut.context.ApplicationContext;

/**
 * Base for InfluxDB tests.
 *
 * @author Stephan Schnabel
 */
public abstract class AbstractTest {

	private static final String INFLUXDB_ORG = UUID.randomUUID().toString();
	private static final String INFLUXDB_BUCKET = "default";
	private static final String INFLUXDB_TOKEN = UUID.randomUUID().toString();
	private static final String INFLUXDB_USERNAME = UUID.randomUUID().toString();
	private static final String INFLUXDB_PASSWORD = UUID.randomUUID().toString();

	private static final DockerImageName INFLUXDB_IMAGE = DockerImageName.parse("influxdb:2.0-alpine");
	private static final GenericContainer<?> INFLUXDB = new GenericContainer<>(INFLUXDB_IMAGE)
			.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
			.withEnv("DOCKER_INFLUXDB_INIT_ORG", INFLUXDB_ORG)
			.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", INFLUXDB_BUCKET)
			.withEnv("DOCKER_INFLUXDB_INIT_TOKEN", INFLUXDB_TOKEN)
			.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", INFLUXDB_USERNAME)
			.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", INFLUXDB_PASSWORD)
			.withExposedPorts(8086)
			.waitingFor(Wait.forListeningPort());

	@BeforeAll
	static void start() {
		// see https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control
		if (!INFLUXDB.isRunning()) {
			INFLUXDB.start();
		}
	}

	void run(Map<String, Object> properties, Consumer<ApplicationContext> executable) {

		var tmp = new HashMap<>(Map.<String, Object>of(
				"influxdb.url", "http://" + INFLUXDB.getHost() + ":" + INFLUXDB.getMappedPort(8086),
				"influxdb.org", INFLUXDB_ORG,
				"influxdb.bucket", INFLUXDB_BUCKET,
				"influxdb.token", INFLUXDB_TOKEN,
				"influxdb.username", INFLUXDB_USERNAME,
				"influxdb.password", INFLUXDB_PASSWORD));
		tmp.putAll(properties);
		System.out.println(tmp);
		try (var context = ApplicationContext.builder().banner(false).properties(tmp).start()) {
			executable.accept(context);
		}
	}
}
