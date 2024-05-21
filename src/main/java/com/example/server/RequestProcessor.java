package com.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class RequestProcessor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DatabaseManager databaseManager = new DatabaseManager();

    public static String processRequest(String request) {
        String[] lines = request.split("\r\n");
        String firstLine = lines[0];
        if (firstLine.startsWith("GET")) {
            return handleGetRequest(firstLine);
        } else if (firstLine.startsWith("POST")) {
            return handlePostRequest(lines);
        } else if (firstLine.startsWith("PUT")) {
            return handlePutRequest(lines);
        } else if (firstLine.startsWith("DELETE")) {
            return handleDeleteRequest(lines);
        }
        return "{\"status\": \"error\", \"message\": \"Invalid request\"}";
    }

    private static String handleGetRequest(String request) {
        // Пример: "GET /students HTTP/1.1"
        String[] parts = request.split(" ");
        if (parts.length < 2 || !parts[1].equals("/students")) {
            return "{\"status\": \"error\", \"message\": \"Invalid endpoint\"}";
        }

        try {
            List<Student> students = databaseManager.getAllStudents();
            return objectMapper.writeValueAsString(students);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to fetch students\"}";
        }
    }

    private static String handlePostRequest(String[] requestLines) {
        try {
            String jsonData = extractJsonData(requestLines);
            Student student = objectMapper.readValue(jsonData, Student.class);
            databaseManager.addStudent(student);
            return "{\"status\": \"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to add student\"}";
        }
    }

    private static String handlePutRequest(String[] requestLines) {
        try {
            String jsonData = extractJsonData(requestLines);
            Student student = objectMapper.readValue(jsonData, Student.class);
            databaseManager.updateStudent(student);
            return "{\"status\": \"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to update student\"}";
        }
    }

    private static String handleDeleteRequest(String[] requestLines) {
        try {
            String jsonData = extractJsonData(requestLines);
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(jsonData);
            int studentId = jsonNode.get("id").asInt();
            databaseManager.deleteStudent(studentId);
            return "{\"status\": \"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\": \"error\", \"message\": \"Failed to delete student\"}";
        }
    }

    private static String extractJsonData(String[] requestLines) {
        StringBuilder jsonData = new StringBuilder();
        boolean isJsonPart = false;
        for (String line : requestLines) {
            if (isJsonPart) {
                jsonData.append(line);
            }
            if (line.isEmpty()) {
                isJsonPart = true;
            }
        }
        return jsonData.toString().trim();
    }
}
