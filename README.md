# Micronaut InfluxDB support

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/kokuwaio/micronaut-influxdb.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/io.kokuwa.micronaut/micronaut-influxdb.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/io.kokuwa.micronaut)
[![CI](https://img.shields.io/github/actions/workflow/status/kokuwaio/micronaut-influxdb/ci.yaml?branch=main&label=CI)](https://github.com/kokuwaio/micronaut-influxdb/actions/workflows/ci.yaml?query=branch%3Amain)

Include in your `pom.xml`:

```xml
<dependency>
  <groupId>io.kokuwa.micronaut</groupId>
  <artifactId>micronaut-influxdb</artifactId>
  <version>${version.io.kokuwa.micronaut.influxdb}</version>
  <scope>runtime</scope>
</dependency>
```

## Features

* factory for blocking/rx client
* health indicator for micronaut health endpoint

## Properties

| Parameter | Description | Default |
|---|---|---|
| `influxdb.enabled` | create InfluxDB clients | `true` |
| `influxdb.url` | connection url of InfluxDB | `http://influxdb:8086` |
| `influxdb.token` | token for auth | `changeMe` |
| `influxdb.organisation` | organisation in InfluxDB | `default` |
| `influxdb.bucket` | bucket within organisation | `default` |
| `influxdb.log-level` | InfluxDB log level ([values](https://github.com/influxdata/influxdb-client-java/blob/master/client-core/src/main/java/com/influxdb/LogLevel.java#L27)) | `NONE` |
| `influxdb.health.enabled` | enable health indicator | `true` |
