package io.kokuwa.micronaut.influxdb;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.client.InfluxDBClient;

import io.micronaut.context.annotation.Requires;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

/**
 * A {@link HealthIndicator} for InfluxDB.
 *
 * @author Stephan Schnabel
 */
@Requires(property = "influxdb.health.enabled", notEquals = "false")
@Requires(beans = InfluxDBClient.class)
@Singleton
public class InfluxDBHealthIndicator implements HealthIndicator {

	private final Logger log = LoggerFactory.getLogger(InfluxDBHealthIndicator.class);
	private final InfluxDBClient influxdb;

	public InfluxDBHealthIndicator(InfluxDBClient influxdb) {
		this.influxdb = influxdb;
	}

	@Override
	public Publisher<HealthResult> getResult() {
		return Mono.fromCallable(() -> getHealthResult());
	}

	private HealthResult getHealthResult() {

		var ready = influxdb.ready();
		if (ready == null) {
			log.warn("Failed to ping to InfluxDB");
		} else {
			log.trace("InfluxDB ready {}", ready);
		}

		return HealthResult
				.builder("influxdb")
				.status(ready == null ? HealthStatus.DOWN : HealthStatus.UP)
				.details(ready)
				.build();
	}
}
