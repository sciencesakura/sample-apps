# spring-security-user-passwd-auth

The sample app with Spring Security's username and password authentication.

## Keywords

- Spring Boot 3.1
- Spring Security 6.1

## Requirements

- Java 20+

## How to build

```shell
$ ./mvnw clean package -DskipTests
```

Then you will get the artifact `./webapi/target/webapi.jar` .

## How to run

```shell
$ java -jar ./webapi/target/webapi.jar
```

Then the web API server will start on port 50001.
