# Micronaut InfluxDB support

Supports only InfluxDB 2.x.

## Features

* factory for blocking/rx client
* health indicator for micronaut health endpoint

## Properties

| Parameter | Description | Default |
|---|---|---|
| `influxdb.health.enabled` | **mandataory** connection url of InfluxDB | |
| `influxdb.org` | **mandataory** organisation in InfluxDB | |
| `influxdb.bucket` | bucket within organisation | `default` |
| `influxdb.log-level` | InfluxDB log level ([values](https://github.com/influxdata/influxdb-client-java/blob/master/client-core/src/main/java/com/influxdb/LogLevel.java#L27)) | `NONE` |
| `influxdb.token` | token for auth| |
| `influxdb.username` | username for basic auth| |
| `influxdb.password` | password for basic auth | |
| `influxdb.health.enabled` | enable health indicator | `true` |

It is mandatory to provide `influxdb.token` or  `influxdb.username`/`influxdb.password` for authentication. For production use `influxdb.token` because otherwise a sessions is created that will not survive InfluxDB restarts!

## Build & Release

### Dependency updates

Display dependency updates:

```sh
mvn versions:display-property-updates -U
```

Update dependencies:

```sh
mvn versions:update-properties
```

### Release locally

Run:

```sh
mvn release:prepare release:perform release:clean -B -DreleaseProfiles=oss-release
```
