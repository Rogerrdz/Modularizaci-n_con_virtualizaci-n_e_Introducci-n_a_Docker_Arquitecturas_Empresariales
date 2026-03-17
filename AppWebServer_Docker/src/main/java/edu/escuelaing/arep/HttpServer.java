package edu.escuelaing.arep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpServer {

    private static String staticFilesPath = "webroot";
    private static volatile boolean running = true;
    private static ServerSocket serverSocket;
    private static ExecutorService requestPool;

    private static final AtomicInteger WORKER_COUNTER = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        int port = resolvePort();
        int workers = Math.max(8, Runtime.getRuntime().availableProcessors() * 2);

        requestPool = Executors.newFixedThreadPool(workers, runnable -> {
            Thread worker = new Thread(runnable);
            worker.setName("http-worker-" + WORKER_COUNTER.getAndIncrement());
            return worker;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Recibida señal de apagado, cerrando servidor...");
            shutdownGracefully();
        }));

        serverSocket = new ServerSocket(port);
        System.out.println("Servidor HTTP iniciado en http://localhost:" + port);

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                requestPool.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }

        shutdownGracefully();
    }

    private static void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             OutputStream out = socket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) {
                sendTextResponse(out, 400, "Bad Request", "Solicitud inválida");
                return;
            }

            URI uri;
            try {
                uri = new URI(tokens[1]);
            } catch (URISyntaxException e) {
                sendTextResponse(out, 400, "Bad Request", "URI inválida");
                return;
            }

            String reqpath = uri.getPath() == null || uri.getPath().isBlank() ? "/" : uri.getPath();
            Map<String, String> parameters = parseQueryParams(uri.getRawQuery());

            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                // Ignorar headers por ahora.
            }

            if ("/shutdown".equals(reqpath)) {
                sendTextResponse(out, 200, "OK", "El servidor se ha apagado de forma elegante...");
                shutdownGracefully();
                return;
            }

            HttpRequest request = new HttpRequest(parameters);
            WebMethod handler = WebFramework.getRoute(reqpath);

            if (handler != null) {
                String result = handler.execute(request, null);
                String body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Respuesta</title></head><body>"
                        + result + "</body></html>";
                sendBinaryResponse(out, 200, "OK", "text/html; charset=UTF-8", body.getBytes(StandardCharsets.UTF_8));
            } else {
                serveStaticFile(reqpath, out);
            }
        } catch (Exception e) {
            System.err.println("Error procesando cliente: " + e.getMessage());
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> parameters = new HashMap<>();
        if (query == null || query.isBlank()) {
            return parameters;
        }

        for (String param : query.split("&")) {
            if (param.isBlank()) {
                continue;
            }
            String[] kv = param.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            parameters.put(key, value);
        }

        return parameters;
    }

    private static void serveStaticFile(String reqpath, OutputStream out) throws IOException {
        String filePath = staticFilesPath + (reqpath.equals("/") ? "/index.html" : reqpath);
        String contentType = getContentType(filePath);

        try (InputStream fileStream = HttpServer.class.getResourceAsStream("/" + filePath)) {
            if (fileStream == null) {
                sendTextResponse(out, 404, "Not Found", "Archivo no encontrado: " + reqpath);
                return;
            }

            byte[] fileBytes = fileStream.readAllBytes();
            sendBinaryResponse(out, 200, "OK", contentType, fileBytes);
        }
    }

    private static void sendTextResponse(OutputStream out, int statusCode, String statusText, String body) throws IOException {
        sendBinaryResponse(out, statusCode, statusText, "text/plain; charset=UTF-8", body.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendBinaryResponse(OutputStream out, int statusCode, String statusText,
                                           String contentType, byte[] body) throws IOException {
        String headers = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    public static synchronized void shutdownGracefully() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando el socket del servidor: " + e.getMessage());
        }

        if (requestPool != null && !requestPool.isShutdown()) {
            requestPool.shutdown();
            if (!Thread.currentThread().getName().startsWith("http-worker-")) {
                try {
                    if (!requestPool.awaitTermination(5, TimeUnit.SECONDS)) {
                        requestPool.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    requestPool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static int resolvePort() {
        String envPort = System.getenv("PORT");
        if (envPort == null || envPort.isBlank()) {
            return 8082;
        }

        try {
            return Integer.parseInt(envPort);
        } catch (NumberFormatException e) {
            System.err.println("PORT inválido ('" + envPort + "'), usando 8082.");
            return 8082;
        }
    }

    private static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css"))  return "text/css";
        if (filePath.endsWith(".js"))   return "application/javascript";
        if (filePath.endsWith(".png"))  return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".ico"))  return "image/x-icon";
        return "text/plain";
    }

    public static void get(String path, WebMethod wm) {
        WebFramework.get(path, wm);
    }

    public static void staticfiles(String path) {
        staticFilesPath = path;
    }
}