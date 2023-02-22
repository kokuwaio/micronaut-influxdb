package io.kokuwa.micronaut.influxdb;

import java.util.Optional;

import com.influxdb.LogLevel;
import com.influxdb.client.BucketsApi;
import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.InfluxQLQueryApi;
import com.influxdb.client.LabelsApi;
import com.influxdb.client.OrganizationsApi;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.WriteOptions;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import okhttp3.OkHttpClient;

/**
 * Factory for InfluxDB client.
 *
 * @author Stephan Schnabel
 */
@Requires(property = "influxdb.enabled", notEquals = "false")
@Factory
public class InfluxDBFactory {

	@Singleton
	@Secondary
	InfluxDBClientOptions options(
			@Value("${influxdb.url:`http://influxdb:8086`}") String url,
			@Value("${influxdb.token:changeMe}") char[] token,
			@Value("${influxdb.organisation:default}") String organisation,
			@Value("${influxdb.bucket:default}") String bucket,
			@Value("${influxdb.log-level:NONE}") LogLevel logLevel,
			Optional<OkHttpClient.Builder> okHttp) {
		var builder = InfluxDBClientOptions.builder();
		okHttp.ifPresent(builder::okHttpClient);
		return builder
				.url(url)
				.authenticateToken(token)
				.org(organisation)
				.bucket(bucket)
				.logLevel(logLevel)
				.build();
	}

	@Context
	@Bean(preDestroy = "close")
	InfluxDBClient client(InfluxDBClientOptions options) {
		return InfluxDBClientFactory.create(options);
	}

	@Singleton
	BucketsApi buckets(InfluxDBClient client) {
		return client.getBucketsApi();
	}

	@Singleton
	DeleteApi delete(InfluxDBClient client) {
		return client.getDeleteApi();
	}

	@Singleton
	QueryApi query(InfluxDBClient client) {
		return client.getQueryApi();
	}

	@Singleton
	InfluxQLQueryApi influxQuery(InfluxDBClient client) {
		return client.getInfluxQLQueryApi();
	}

	@Singleton
	OrganizationsApi organizations(InfluxDBClient client) {
		return client.getOrganizationsApi();
	}

	@Singleton
	LabelsApi labels(InfluxDBClient client) {
		return client.getLabelsApi();
	}

	@Singleton
	@Secondary
	WriteOptions writeOptions() {
		return WriteOptions.DEFAULTS;
	}

	@Singleton
	WriteApi write(InfluxDBClient client, WriteOptions options) {
		return client.makeWriteApi(options);
	}

	@Singleton
	WriteApiBlocking writeBlocking(InfluxDBClient client) {
		return client.getWriteApiBlocking();
	}
}
