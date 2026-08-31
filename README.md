# java_assessment

Java Quarkus fulfillment monolith implementing warehouse CRUD, store sync via CDI events, location resolution, and warehouse-product-store fulfillment constraints. Includes JPA/Hibernate, H2 integration tests, JaCoCo 80% coverage gate, REST exception mappers, OpenAPI specs, Docker support, and a GitHub Actions CI/CD workflow.

## About the assignment

You will find the tasks of this assignment in the [CODE_ASSIGNMENT](java-assignment/CODE_ASSIGNMENT.md) file.

## About the code base

Some of this code is based on https://github.com/quarkusio/quarkus-quickstarts.

### Requirements

- JDK 17+
- Maven (wrapper included)

### Running the tests

```sh
cd java-assignment
./mvnw test
```

### Building the project

```sh
cd java-assignment
./mvnw package
```
