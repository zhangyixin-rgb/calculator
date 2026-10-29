# =====================================================================
# backend/Dockerfile —— 计算器后端（HTTP 计算 API）镜像（Lab 7）
#
# 多阶段构建：
#   阶段 1（build）  : maven + JDK 21 镜像中编译并打包可执行 fat jar
#   阶段 2（runtime）: 只含 JRE 的精简镜像，仅拷贝 jar，最终镜像更小
# =====================================================================

# ------------------- 阶段 1：构建 -------------------
# maven:3.9-eclipse-temurin-21 自带 Maven 3.9 与 OpenJDK 21，
# 与 backend/pom.xml 中 maven.compiler.release=21 保持一致。
FROM maven:3.9-eclipse-temurin-21 AS build

# 工作目录，后续 RUN / COPY 都在 /app 下执行
WORKDIR /app

# 先只拷贝 pom.xml：依赖未变化时可以命中 Maven 层缓存，
# 不用每次构建都重新下载依赖。
COPY pom.xml .

# 预先下载全部依赖（单元测试已在 CI 阶段执行，这里跳过测试）
RUN mvn -B dependency:go-offline

# 拷贝源码，并用 maven-shade-plugin 打出内嵌 commons-lang3 的 fat jar
COPY src ./src
RUN mvn -B package -DskipTests

# ------------------- 阶段 2：运行 -------------------
# eclipse-temurin:21-jre 只包含 Java 21 运行时，没有编译器与构建工具
FROM eclipse-temurin:21-jre

# 创建专用非 root 用户来运行应用（容器安全最佳实践）
RUN useradd --system --create-home --uid 10001 appuser

WORKDIR /app

# 从构建阶段拷贝 fat jar，避免把 Maven 和源码带进运行镜像
COPY --from=build /app/target/calculator-1.0.jar app.jar

# 切换为非 root 用户
USER appuser

# 声明后端 HTTP 服务监听的端口，compose 中映射为 "8000:8000"
EXPOSE 8000

# 启动 HTTP API 服务：java -jar app.jar
# JSON 数组形式不经过 shell，参数不会被通配符/引号解析干扰
ENTRYPOINT ["java", "-jar", "app.jar"]
