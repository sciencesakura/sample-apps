# spring-openapi-generator

The sample app for Spring Framework with OpenAPI Generator.

## Keywords

- Spring Boot 3.1
- OpenAPI Generator 7.0

## Requirements

- Java 20+

## How to build

```shell
$ ./mvnw clean package -DskipTests
```

Then you will get the artifact `./api-impl/target/api-impl.jar` .

## How to run

```shell
$ java -jar ./api-impl/target/api-impl.jar
```

Then the web API server will start on port 50001.
