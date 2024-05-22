package com.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestProcessor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final DatabaseManager databaseManager = new DatabaseManager();

    public String processRequest(String request) {
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
        // Пример: "GET /students?name=Иван HTTP/1.1"
        String[] parts = request.split(" ");
        if (parts.length < 2 || !parts[1].startsWith("/students")) {
            return "{\"status\": \"error\", \"message\": \"Invalid endpoint\"}";
        }

        // Проверяем, есть ли параметр фильтрации по имени в URL
        Map<String, String> queryParams = parseQueryParams(parts[1].substring(parts[1].indexOf('?') + 1));
        if (queryParams.containsKey("first_name")) {
            String filterName = queryParams.get("first_name");
            try {
                List<Student> students = DatabaseManager.getStudentsByName(filterName);
                return objectMapper.writeValueAsString(students);
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"status\": \"error\", \"message\": \"Failed to fetch students\"}";
            }
        } else {
            try {
                List<Student> students = DatabaseManager.getAllStudents();
                return objectMapper.writeValueAsString(students);
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"status\": \"error\", \"message\": \"Failed to fetch students\"}";
            }
        }
    }

    private static Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> params = new HashMap<>();
        String[] keyValuePairs = queryParams.split("&");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                params.put(key, value);
            }
        }
        return params;
    }


    private String handlePostRequest(String[] requestLines) {
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

    private String handlePutRequest(String[] requestLines) {
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

    private String handleDeleteRequest(String[] requestLines) {
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

    private String extractJsonData(String[] requestLines) {
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
