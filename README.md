# Calculator

[![Calculator CI](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml/badge.svg)](https://github.com/zhangyixin-rgb/calculator/actions/workflows/ci-calculator.yml)

A calculator built with Apache Maven, developed for the *Software Development
Tools* course (Labs 3-7). Lab 7 turns it into a two-container application:

- **backend** – the original Java calculation logic exposed as an HTTP API on port 8000
- **web** – a static calculator page served by nginx on port 80, which proxies
  `/api/...` to the `backend` service

## Features

- Addition, subtraction, multiplication and division
- Input validation: blank input, unknown operator and division by zero
- Uses `commons-lang3` for numeric input validation
- Packaged as a runnable "fat jar" by the Maven Shade plugin
- Lightweight HTTP API built on the JDK's `com.sun.net.httpserver` (no extra framework)

## Project layout

```
calculator/
├── backend/                       # calculator backend service (port 8000)
│   ├── Dockerfile                 # multi-stage build
│   ├── pom.xml
│   ├── .dockerignore
│   └── src
│       ├── main/java/com/zhangyixin/
│       │   ├── Calculator.java    # calculation logic (unchanged since Lab 6)
│       │   ├── HttpMain.java      # HTTP entry point (Lab 7)
│       │   └── App.java           # command-line entry point
│       └── test/java/com/zhangyixin/
│           └── CalculatorTest.java
├── web/                           # calculator front end (port 80)
│   ├── Dockerfile
│   ├── nginx.conf                 # proxies /api/ to http://backend:8000
│   ├── index.html
│   ├── app.js
│   └── style.css
├── docker-compose.yml             # multi-container orchestration (Lab 7)
├── README.md
└── .github/workflows/ci-calculator.yml
```

## Build and test locally

> Requires JDK 21 (the project targets Java 21) and Maven 3.9+.

```bash
cd backend
mvn clean test        # compile everything and run all JUnit 5 tests
mvn clean package     # build the runnable fat jar
java -jar target/calculator-1.0.jar          # start the HTTP API on port 8000
java -cp target/calculator-1.0.jar com.zhangyixin.App 2 + 2   # CLI mode
```

API examples:

```bash
curl http://127.0.0.1:8000/api/health
curl "http://127.0.0.1:8000/api/calc?a=6&op=%2A&b=7"
```

## Docker Compose (Lab 7)

Both services are built from their own Dockerfile inside the repository
(`build:` with a local context), then started together:

```bash
docker compose up -d --build     # build images and start backend + web
docker compose ps                # check container status and health
docker compose logs -f           # follow the logs of both services
```

Then open <http://127.0.0.1:8080> in a browser. Stop and remove everything with:

```bash
docker compose down
```

### Ports and networking

| Service | Container port | Host port | Notes |
|---------|----------------|-----------|-------|
| backend | 8000 | 8000 | Java HTTP API |
| web     | 80 | 8080 | nginx + static page |

Inside the compose network the front end talks to the backend through the
service name (`http://backend:8000`) in `web/nginx.conf`. The browser itself
only requests the relative path `/api/...`, because a browser running on the
host cannot resolve a Docker-internal hostname such as `backend`.

`web` uses `depends_on: condition: service_healthy`, so it waits until the
backend health check passes instead of only waiting for the container to start.

## Continuous Integration

Every push to `main` and every pull request targeting `main` automatically runs
the GitHub Actions workflow defined in
[`.github/workflows/ci-calculator.yml`](.github/workflows/ci-calculator.yml):

1. **build** – compile and test `backend/` in parallel on JDK 17 and JDK 21.
2. **docker-build** – build the backend image after the tests pass and push it
   to Docker Hub only when code lands on `main`.
3. **compose-validate** – check out the full repository, run
   `docker compose config -q` and `docker compose build` to prove that the
   compose file and both Dockerfiles are valid. No containers are started in CI.

## Docker Hub

The backend image is published as
[`20119445/calculator`](https://hub.docker.com/r/20119445/calculator).
