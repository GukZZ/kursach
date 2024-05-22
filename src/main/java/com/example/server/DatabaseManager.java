package com.example.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db?useUnicode=yes&characterEncoding=UTF-8";
    private static final String USER = "a1";
    private static final String PASSWORD = "Gukin1489..";

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM student")) {
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setMiddleName(resultSet.getString("middle_name"));
                student.setGroupName(resultSet.getString("group_name"));
                student.setAge(resultSet.getInt("age"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static List<Student> getStudentsByName(String name) {
        List<Student> students = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM student WHERE first_name LIKE ?")) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getInt("id"));
                student.setFirstName(resultSet.getString("first_name"));
                student.setLastName(resultSet.getString("last_name"));
                student.setMiddleName(resultSet.getString("middle_name"));
                student.setGroupName(resultSet.getString("group_name"));
                student.setAge(resultSet.getInt("age"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void addStudent(Student student) {
        String sql = "INSERT INTO student (first_name, last_name, middle_name, group_name, age) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getMiddleName());
            statement.setString(4, student.getGroupName());
            statement.setInt(5, student.getAge());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, middle_name = ?, group_name = ?, age = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getMiddleName());
            statement.setString(4, student.getGroupName());
            statement.setInt(5, student.getAge());
            statement.setInt(6, student.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}