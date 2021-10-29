package io.kokuwa.micronaut.influxdb;

import java.util.Optional;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * Factory for InfluxDB client.
 *
 * @author Stephan Schnabel
 */
@Requires(property = "influxdb.enabled", notEquals = "false")
@Requires(property = "influxdb.url")
@Factory
@Slf4j
public class InfluxDBFactory {

	@Bean
	InfluxDBClientOptions influxDBClientOptions(InfluxDBProperties properties, Optional<OkHttpClient.Builder> okHttp) {
		var builder = InfluxDBClientOptions.builder();
		if (properties.getToken() != null) {
			builder.authenticateToken(properties.getToken());
		} else {
			log.warn("Do not use username/password in production, use token!");
			builder.authenticate(properties.getUsername(), properties.getPassword());
		}
		if (okHttp.isPresent()) {
			builder.okHttpClient(okHttp.get());
		}
		return builder
				.url(properties.getUrl())
				.org(properties.getOrg())
				.bucket(properties.getBucket())
				.logLevel(properties.getLogLevel())
				.build();
	}

	@Context
	@Bean(preDestroy = "close")
	InfluxDBClient influxDBClient(InfluxDBClientOptions options) {
		log.info("Connecting to influx using url {}.", options.getUrl());
		return InfluxDBClientFactory.create(options);
	}
}
