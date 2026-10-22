# =====================================================================
# Dockerfile for the Calculator (Lab 6)
#
# Multi-stage build (industrial best practice):
#   Stage 1 (build):  a full Maven + JDK image compiles the project
#                     and produces a runnable "fat jar".
#   Stage 2 (runtime): a slim JRE-only image only copies the jar,
#                     so the final image stays small.
# =====================================================================

# ------------------- Stage 1 : build -------------------
# maven:3.9-eclipse-temurin-21 bundles Maven 3.9 with OpenJDK 21,
# which matches our project (pom.xml sets maven.compiler.release=21).
FROM maven:3.9-eclipse-temurin-21 AS build

# WORKDIR creates /app and makes it the working directory for every
# later RUN / COPY / CMD in this stage.
WORKDIR /app

# Copy only pom.xml first: thanks to Maven layer caching the
# dependencies are downloaded once and reused unless pom.xml changes.
COPY pom.xml .

# Download all dependency jars ahead of time (tests are skipped here
# because they already run in the GitHub Actions CI pipeline).
RUN mvn -B dependency:go-offline

# Copy the whole source tree, then build the shaded "fat jar".
# maven-shade-plugin embeds commons-lang3 into the jar and sets the
# Main-Class manifest entry to com.zhangyixin.App.
COPY src ./src
RUN mvn -B package -DskipTests

# ------------------- Stage 2 : runtime -------------------
# eclipse-temurin:21-jre is a slim image containing only the Java 21
# runtime (JRE): no compilers, no build tools, much smaller image.
FROM eclipse-temurin:21-jre

# Create a dedicated non-root user (container security best practice).
RUN useradd --system --create-home --uid 10001 appuser

# WORKDIR creates /app for the runtime stage too.
WORKDIR /app

# COPY --from=build copies the jar produced in stage 1 into this
# slim runtime image.
COPY --from=build /app/target/calculator-1.0.jar app.jar

# Switch to the non-root user created above.
USER appuser

# ENTRYPOINT defines the fixed command that always runs when the
# container starts: java -jar app.jar
# The JSON-array form runs without a shell, so arguments are passed
# through untouched (no quoting / wildcard expansion problems).
ENTRYPOINT ["java", "-jar", "app.jar"]

# CMD provides default arguments for ENTRYPOINT; here a demo
# multiplication "6 * 7". Any arguments given on the docker run
# command line replace these defaults, e.g. "docker run calculator:v1 2 + 3".
CMD ["6", "*", "7"]
