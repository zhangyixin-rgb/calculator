# Calculator

[![Calculator CI](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml/badge.svg)](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml)

A simple command-line calculator built with Apache Maven, developed for the
*Software Development Tools* course (Labs 3-6).

## Features

- Addition, subtraction, multiplication and division
- Input validation: blank input, unknown operator and division by zero
- Uses `commons-lang3` for numeric input validation
- Packaged as a runnable "fat jar" by the Maven Shade plugin

## Project layout

```
calculator/
├── pom.xml
├── README.md
├── Dockerfile                # multi-stage build (Lab 6)
├── .dockerignore
├── .github/workflows/ci-calculator.yml
└── src
    ├── main/java/com/zhangyixin/
    │   ├── App.java           # command-line entry point
    │   └── Calculator.java    # calculation logic
    └── test/java/com/zhangyixin/
        └── CalculatorTest.java  # 7 JUnit 5 test cases
```

## Build and test locally

> Requires JDK 21 (the project targets Java 21) and Maven 3.9+.

```bash
mvn clean test        # compile everything and run all JUnit 5 tests
mvn clean package     # build the runnable fat jar
java -cp target/calculator-1.0.jar com.zhangyixin.App 2 + 2
```

## Continuous Integration

Every push to `main` and every pull request targeting `main` automatically runs
the GitHub Actions workflow defined in
[`.github/workflows/ci-calculator.yml`](.github/workflows/ci-calculator.yml).
The workflow builds and tests the project in parallel on JDK 17 and JDK 21
(a build matrix), caches the Maven dependencies, resolves the declared
dependencies, compiles all Java sources and runs all 7 JUnit 5 test cases.
The badge at the top of this file shows the status of the latest run.

## Docker (Lab 6)

The project is containerised with a multi-stage `Dockerfile`: the build stage
compiles the fat jar inside a `maven:3.9-eclipse-temurin-21` image, and the
runtime stage copies only the jar into the slim `eclipse-temurin:21-jre` image,
so the final image stays small (~116 MB).

```bash
docker build -t calculator:v1 .
docker run --rm calculator:v1            # default demo: 6 * 7
docker run --rm calculator:v1 20 / 5     # pass your own arguments
```

The image is published on Docker Hub as
[`20119445/calculator:lab6`](https://hub.docker.com/r/20119445/calculator).
The CI workflow now also builds the Docker image after the unit tests pass and
pushes it to Docker Hub automatically whenever code is merged into `main`.
