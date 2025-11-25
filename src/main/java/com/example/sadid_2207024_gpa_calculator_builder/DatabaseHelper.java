package com.example.sadid_2207024_gpa_calculator_builder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:cgpa_records.db";
    private static final Gson gson = new Gson();
    private static Connection connection;

    // Initialize database connection and create table if not exists
    public static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            createTableIfNotExists();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create student_records table
    private static void createTableIfNotExists() {
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS student_records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_name TEXT NOT NULL,
                    student_id TEXT NOT NULL,
                    total_courses INTEGER NOT NULL,
                    total_credits REAL NOT NULL,
                    cgpa REAL NOT NULL,
                    average_grade TEXT NOT NULL,
                    record_date TEXT NOT NULL,
                    courses_json TEXT NOT NULL
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Table 'student_records' checked/created.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Convert Course list to JSON
    public static String coursesToJson(List<Course> courses) {
        return gson.toJson(courses);
    }

    // Convert JSON to Course list
    public static List<Course> jsonToCourses(String json) {
        Type listType = new TypeToken<List<Course>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    // Ensure connection is active
    private static void ensureConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL);
                System.out.println("Database connection re-established.");
            }
        } catch (SQLException e) {
            System.err.println("Error ensuring database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Insert a new student record
    public static boolean insertStudentRecord(StudentRecord record, List<Course> courses) {
        try {
            System.out.println("=== Starting insertStudentRecord ===");
            ensureConnection();
            System.out.println("Connection ensured");

            String insertSQL = """
                    INSERT INTO student_records (student_name, student_id, total_courses, 
                    total_credits, cgpa, average_grade, record_date, courses_json)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
                System.out.println("PreparedStatement created");
                pstmt.setString(1, record.getStudentName());
                pstmt.setString(2, record.getStudentId());
                pstmt.setInt(3, record.getTotalCourses());
                pstmt.setDouble(4, record.getTotalCredits());
                pstmt.setDouble(5, record.getCgpa());
                pstmt.setString(6, record.getAverageGrade());
                pstmt.setString(7, record.getRecordDate());

                String coursesJson = coursesToJson(courses);
                System.out.println("Courses JSON length: " + coursesJson.length());
                pstmt.setString(8, coursesJson);

                System.out.println("Executing insert...");
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("SQL Error inserting record: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            System.err.println("General error in insertStudentRecord: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get all student records
    public static ObservableList<StudentRecord> getAllStudentRecords() {
        ensureConnection();

        ObservableList<StudentRecord> records = FXCollections.observableArrayList();
        String selectSQL = "SELECT * FROM student_records ORDER BY id DESC";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(selectSQL)) {

            while (rs.next()) {
                StudentRecord record = new StudentRecord(
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("student_id"),
                        rs.getInt("total_courses"),
                        rs.getDouble("total_credits"),
                        rs.getDouble("cgpa"),
                        rs.getString("average_grade"),
                        rs.getString("record_date")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching records: " + e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    // Get a single student record by ID with courses
    public static StudentRecord getStudentRecordById(int recordId) {
        ensureConnection();

        String selectSQL = "SELECT * FROM student_records WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                StudentRecord record = new StudentRecord(
                        rs.getInt("id"),
                        rs.getString("student_name"),
                        rs.getString("student_id"),
                        rs.getInt("total_courses"),
                        rs.getDouble("total_credits"),
                        rs.getDouble("cgpa"),
                        rs.getString("average_grade"),
                        rs.getString("record_date")
                );

                // Parse courses from JSON
                String coursesJson = rs.getString("courses_json");
                List<Course> courses = jsonToCourses(coursesJson);
                record.setCourses(courses);

                return record;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching record by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Update a student record
    public static boolean updateStudentRecord(StudentRecord record) {
        ensureConnection();

        String updateSQL = """
                UPDATE student_records 
                SET student_name = ?, student_id = ?
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, record.getStudentName());
            pstmt.setString(2, record.getStudentId());
            pstmt.setInt(3, record.getRecordId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete a student record
    public static boolean deleteStudentRecord(int recordId) {
        ensureConnection();

        String deleteSQL = "DELETE FROM student_records WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, recordId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get current timestamp
    public static String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
