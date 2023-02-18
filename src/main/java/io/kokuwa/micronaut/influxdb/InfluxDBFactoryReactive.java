package io.kokuwa.micronaut.influxdb;

import com.influxdb.client.InfluxDBClientOptions;
import com.influxdb.client.reactive.InfluxDBClientReactive;
import com.influxdb.client.reactive.InfluxDBClientReactiveFactory;
import com.influxdb.client.reactive.QueryReactiveApi;
import com.influxdb.client.reactive.WriteOptionsReactive;
import com.influxdb.client.reactive.WriteReactiveApi;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;

/**
 * Factory for reactive InfluxDB client.
 *
 * @author Stephan Schnabel
 */
@Requires(property = "influxdb.enabled", notEquals = "false")
@Requires(classes = InfluxDBClientReactive.class)
@Requires(bean = InfluxDBClientOptions.class)
@Factory
public class InfluxDBFactoryReactive {

	@Singleton
	@Bean(preDestroy = "close")
	InfluxDBClientReactive client(InfluxDBClientOptions options) {
		return InfluxDBClientReactiveFactory.create(options);
	}

	@Singleton
	QueryReactiveApi query(InfluxDBClientReactive client) {
		return client.getQueryReactiveApi();
	}

	@Singleton
	@Secondary
	WriteOptionsReactive writeOptions() {
		return WriteOptionsReactive.DEFAULTS;
	}

	@Singleton
	WriteReactiveApi write(InfluxDBClientReactive client, WriteOptionsReactive options) {
		return client.getWriteReactiveApi(options);
	}
}
