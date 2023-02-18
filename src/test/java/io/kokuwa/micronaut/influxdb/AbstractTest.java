package io.kokuwa.micronaut.influxdb;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.micronaut.context.ApplicationContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

/**
 * Base for InfluxDB tests.
 *
 * @author Stephan Schnabel
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
public abstract class AbstractTest implements TestPropertyProvider {

	private static Map<String, String> properties;

	public void run(Map<String, String> propertiesOverrides, Consumer<ApplicationContext> executable) {
		var propertiesForTestCase = new HashMap<String, Object>(properties);
		propertiesForTestCase.putAll(propertiesOverrides);
		try (var context = ApplicationContext.builder().banner(false).properties(propertiesForTestCase).start()) {
			executable.accept(context);
		}
	}

	@Override
	@SuppressWarnings("resource")
	public Map<String, String> getProperties() {
		if (properties == null) {

			var influxOrganisaton = "test-organisaton";
			var influxBucket = "default";
			var influxToken = "changeMe";
			var influxContainer = new GenericContainer<>(DockerImageName.parse("influxdb:2.6.1"))
					.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
					.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
					.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
					.withEnv("DOCKER_INFLUXDB_INIT_ORG", influxOrganisaton)
					.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", influxBucket)
					.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", influxToken)
					.withExposedPorts(8086)
					.waitingFor(Wait.forListeningPort());

			influxContainer.start();

			properties = Map.of(
					"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
					"influxdb.token", influxToken,
					"influxdb.organisaton", influxOrganisaton,
					"influxdb.bucket", influxBucket);
		}
		return properties;
	}
}
