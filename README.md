# Calculator

[![Calculator CI](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml/badge.svg)](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml)

A simple command-line calculator built with Apache Maven, developed for the
*Software Development Tools* course (Labs 3-5).

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
The workflow sets up JDK 21, caches the Maven dependencies, resolves the
declared dependencies, compiles all Java sources and runs all 7 JUnit 5 test
cases. The badge at the top of this file shows the status of the latest run.
