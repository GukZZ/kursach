package com.example.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {
    private final SocketChannel clientSocket;

    public ClientHandler(SocketChannel clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientSocket.read(buffer);
            String request = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            if (request.startsWith("GET /") && !request.startsWith("GET /students")) {
                handleStaticFiles(request);
            } else {
                handleApiRequests(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleStaticFiles(String request) {
        try {
            String[] parts = request.split(" ");
            String filePath = "src/main/resources/static" + parts[1];
            if (filePath.equals("src/main/resources/static/")) {
                filePath += "index.html";
            }
            Path path = Paths.get(filePath);

            if (Files.exists(path)) {
                byte[] fileBytes = Files.readAllBytes(path);
                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Length: " + fileBytes.length + "\r\n" +
                        "Content-Type: " + getContentType(filePath) + "\r\n" +
                        "Charset: utf-8\r\n" +
                        "\r\n";
                clientSocket.write(ByteBuffer.wrap(httpResponse.getBytes(StandardCharsets.UTF_8)));
                clientSocket.write(ByteBuffer.wrap(fileBytes));
            } else {
                String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                clientSocket.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleApiRequests(String request) {
        try {
            String response = RequestProcessor.processRequest(request);
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json; charset=utf-8\r\n" +
                    "Content-Length: " + response.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "\r\n" +
                    response;
            clientSocket.write(ByteBuffer.wrap(httpResponse.getBytes(StandardCharsets.UTF_8)));
            clientSocket.write(ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8)));
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html; charset=utf-8";
        } else if (filePath.endsWith(".css")) {
            return "text/css; charset=utf-8";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        } else {
            return "application/octet-stream";
        }
    }
}
