package io.kokuwa.micronaut.influxdb;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.influxdb.LogLevel;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Factory for InfluxDB client.
 *
 * @author Stephan Schnabel
 */
@ConfigurationProperties("influxdb")
@Getter
@Setter
public class InfluxDBProperties {

	@NotNull
	private String url;
	@NotNull
	private String org;
	@NotNull
	private String bucket = "default";
	@NotNull
	private LogLevel logLevel = LogLevel.NONE;

	private char[] token;
	private String username;
	private char[] password;

	@AssertTrue
	boolean isAuthenticationSet() {
		return token != null || username != null && password != null;
	}
}
