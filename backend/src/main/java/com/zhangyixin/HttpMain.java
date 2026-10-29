package com.zhangyixin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 计算器后端的 HTTP 入口（Lab 7）。
 *
 * 使用 JDK 自带的 com.sun.net.httpserver，不引入任何额外框架，
 * 把 Lab 3-6 已有的 Calculator 计算逻辑直接复用为一个 HTTP API：
 *
 *   GET /api/health                        -> {"status":"ok"}
 *   GET /api/calc?a=2&op=%2A&b=3           -> {"result":6.0}
 *   GET /api/calc?a=1&op=%2F&b=0           -> 400 {"error":"division by zero"}
 *
 * 监听端口固定为 8000，容器内通过 compose 的服务名 backend 访问，
 * 对应宿主机映射为 127.0.0.1:8000。
 */
public class HttpMain {

    /** 后端服务监听端口，与 compose 中 "8000:8000" 的容器端口一致 */
    private static final int PORT = 8000;

    /** 日志级别由 compose 的 environment 注入，默认 INFO */
    private static final String LOG_LEVEL =
            System.getenv().getOrDefault("LOG_LEVEL", "INFO").toUpperCase();

    /** 复用原有的计算逻辑，不做任何改动 */
    private static final Calculator CALCULATOR = new Calculator();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/health", HttpMain::handleHealth);
        server.createContext("/api/calc", HttpMain::handleCalc);
        // 使用可伸缩的线程池处理请求，保证在 JDK 17 与 JDK 21 上都能编译运行
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        log("INFO", "calculator backend started on port " + PORT + " (LOG_LEVEL=" + LOG_LEVEL + ")");
    }

    /** 健康检查接口：compose 的 healthcheck 与前端启动时的状态提示都会调用它 */
    private static void handleHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"method not allowed\"}");
            return;
        }
        sendJson(exchange, 200, "{\"status\":\"ok\"}");
    }

    /** 计算接口：查询参数 a、op、b，返回 JSON 结果或错误信息 */
    private static void handleCalc(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"method not allowed\"}");
            return;
        }

        Map<String, String> params = parseQuery(exchange.getRequestURI().getRawQuery());
        String a = params.get("a");
        String op = params.get("op");
        String b = params.get("b");
        if (a == null || op == null || b == null) {
            sendJson(exchange, 400, "{\"error\":\"missing query parameter: a, op and b are required\"}");
            return;
        }

        try {
            double result = CALCULATOR.eval(a, op, b);
            log("INFO", "calc " + a + " " + op + " " + b + " = " + result);
            sendJson(exchange, 200, "{\"result\":" + result + "}");
        } catch (ArithmeticException | IllegalArgumentException e) {
            log("WARN", "calc " + a + " " + op + " " + b + " failed: " + e.getMessage());
            sendJson(exchange, 400, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    /** 解析查询串，并对参数值做 URL 解码（op 中的 *、+ 会被前端编码成 %2A、%2B） */
    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> params = new HashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return params;
        }
        for (String pair : rawQuery.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }

    /** 返回 UTF-8 编码的 JSON，并关闭响应流 */
    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** 按 LOG_LEVEL 过滤日志：DEBUG 级别会打印 INFO 之外的调试信息 */
    private static void log(String level, String message) {
        if (allowed(level)) {
            System.out.println("[" + level + "] " + message);
        }
    }

    private static boolean allowed(String level) {
        int threshold = switch (LOG_LEVEL) {
            case "DEBUG" -> 0;
            case "WARN" -> 2;
            case "ERROR" -> 3;
            default -> 1; // INFO
        };
        int current = switch (level) {
            case "DEBUG" -> 0;
            case "WARN" -> 2;
            case "ERROR" -> 3;
            default -> 1;
        };
        return current >= threshold;
    }

    /** 简单转义，保证错误信息可以安全地放进 JSON 字符串 */
    private static String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
