package com.example.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
            String request = new String(buffer.array()).trim();

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
                        "\r\n";
                clientSocket.write(ByteBuffer.wrap(httpResponse.getBytes()));
                clientSocket.write(ByteBuffer.wrap(fileBytes));
            } else {
                String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                clientSocket.write(ByteBuffer.wrap(response.getBytes()));
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
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n" +
                    response;
            clientSocket.write(ByteBuffer.wrap(httpResponse.getBytes()));
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else {
            return "application/octet-stream";
        }
    }
}
