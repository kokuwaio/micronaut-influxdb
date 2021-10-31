package io.kokuwa.micronaut.influxdb;

import org.reactivestreams.Publisher;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.HealthCheck.StatusEnum;

import io.micronaut.context.annotation.Requires;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * A {@link HealthIndicator} for InfluxDB.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Requires(beans = InfluxDBClient.class)
@Requires(property = "influxdb.health.enabled", value = "true", defaultValue = "true")
@Slf4j
@RequiredArgsConstructor
public class InfluxDBHealthIndicator implements HealthIndicator {

	private final InfluxDBClient influxdb;

	@Override
	public Publisher<HealthResult> getResult() {
		return Mono.fromCallable(() -> getHealthResult());
	}

	private HealthResult getHealthResult() {

		var health = influxdb.health();
		if (health.getStatus() == StatusEnum.FAIL) {
			log.warn("Failed to connect to InfluxDB: {}", health.getMessage());
		} else {
			log.trace("InfluxDB health: {}", health);
		}

		return HealthResult
				.builder("influxdb")
				.status(health.getStatus() == StatusEnum.PASS ? HealthStatus.UP : HealthStatus.DOWN)
				.details(health)
				.build();
	}
}
