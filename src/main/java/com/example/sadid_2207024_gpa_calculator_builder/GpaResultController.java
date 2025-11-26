package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GpaResultController {

    @FXML
    private Label gpaValueLabel;

    @FXML
    private Label gpaPerformanceLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label averageGradeLabel;

    @FXML
    private VBox courseTableBody;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField studentNameField;

    @FXML
    private TextField studentIdField;

    @FXML
    private Button saveRecordButton;

    private List<Course> courses;
    private double totalCreditsValue;

    public void setCoursesData(List<Course> courses, double totalCredits) {
        this.courses = courses;
        this.totalCreditsValue = totalCredits;
        displayResults(totalCredits);
    }

    private void displayResults(double totalCredits) {
        double gpa = GpaCalc.calculateGPA(courses);

        //  GPA
        gpaValueLabel.setText(String.format("%.2f", gpa));

        //  performance label and color
        String performance = getPerformanceLevel(gpa);
        gpaPerformanceLabel.setText(performance);
        gpaPerformanceLabel.setStyle(getPerformanceColor(gpa));

        //  summary
        totalCoursesLabel.setText(String.valueOf(courses.size()));
        totalCreditsLabel.setText(String.format("%.1f", totalCredits));

        //  average grade calc
        String averageGrade = calculateAverageGrade(gpa);
        averageGradeLabel.setText(averageGrade);

        //  course table
        displayCourseTable();

        //  date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        dateLabel.setText("Generated on: " + today.format(formatter));
    }

    private void displayCourseTable() {
        courseTableBody.getChildren().clear();

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            HBox row = createTableRow(i + 1, course);
            courseTableBody.getChildren().add(row);
        }
    }

    private HBox createTableRow(int index, Course course) {
        HBox row = new HBox(0);
        row.setStyle("-fx-background-color: " + (index % 2 == 0 ? "#f9f9f9" : "#ffffff") +
                "; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        row.setPadding(new Insets(10, 12, 10, 12));

        Label numLabel = createTableCell(String.valueOf(index), 50);
        Label nameLabel = createTableCell(course.getCourseName(), 200);
        Label codeLabel = createTableCell(course.getCourseCode(), 80);
        Label creditLabel = createTableCell(String.format("%.1f", course.getCredit()), 60);
        Label teachersLabel = createTableCell(course.getTeacher1() + ", " + course.getTeacher2(), 250);
        Label gradeLabel = createTableCell(course.getGrade(), 60);
        gradeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + getGradeColor(course.getGrade()) + ";");
        Label pointsLabel = createTableCell(String.format("%.2f", course.getGradePoint()), 60);

        row.getChildren().addAll(numLabel, nameLabel, codeLabel, creditLabel, teachersLabel, gradeLabel, pointsLabel);

        return row;
    }

    private Label createTableCell(String text, double width) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setFont(new Font("Arial", 12));
        label.setStyle("-fx-text-fill: #333333;");
        label.setWrapText(true);
        return label;
    }

    private String getPerformanceLevel(double gpa) {
        if (gpa >= 3.75) return "Outstanding Performance!";
        else if (gpa >= 3.5) return "Excellent Work!";
        else if (gpa >= 3.0) return "Good Job!";
        else if (gpa >= 2.7) return "Satisfactory";
        else if (gpa >= 2.0) return "Fair";
        else return "Needs Improvement";
    }

    private String getPerformanceColor(double gpa) {
        if (gpa >= 3.75) return "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
        else if (gpa >= 3.5) return "-fx-text-fill: #2ecc71; -fx-font-weight: bold;";
        else if (gpa >= 3.0) return "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
        else if (gpa >= 2.7) return "-fx-text-fill: #e67e22; -fx-font-weight: bold;";
        else if (gpa >= 2.0) return "-fx-text-fill: #d35400; -fx-font-weight: bold;";
        else return "-fx-text-fill: #c0392b; -fx-font-weight: bold;";
    }

    private String getGradeColor(String grade) {
        switch (grade) {
            case "A+":
            case "A":
                return "#27ae60";
            case "A-":
            case "B+":
                return "#2ecc71";
            case "B":
            case "B-":
                return "#f39c12";
            case "C+":
            case "C":
                return "#e67e22";
            case "D":
                return "#d35400";
            default:
                return "#c0392b";
        }
    }

    private String calculateAverageGrade(double gpa) {
        if (gpa >= 4.0) return "A+";
        else if (gpa >= 3.75) return "A";
        else if (gpa >= 3.5) return "A-";
        else if (gpa >= 3.25) return "B+";
        else if (gpa >= 3.0) return "B";
        else if (gpa >= 2.75) return "B-";
        else if (gpa >= 2.5) return "C+";
        else if (gpa >= 2.25) return "C";
        else if (gpa >= 2.00) return "D";
        else return "F";
    }

    @FXML
    protected void onSaveRecord() {
        String studentName = studentNameField.getText().trim();
        String studentId = studentIdField.getText().trim();

        // Validate inputs
        if (studentName.isEmpty() || studentId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information",
                    "Please enter both student name and student ID before saving.");
            return;
        }

        // Get GPA and other data
        double gpa = GpaCalc.calculateGPA(courses);
        String averageGrade = calculateAverageGrade(gpa);
        String recordDate = DatabaseHelper.getCurrentTimestamp();

        // Create StudentRecord object
        StudentRecord record = new StudentRecord(
                studentName,
                studentId,
                courses.size(),
                totalCreditsValue,
                gpa,
                averageGrade,
                recordDate
        );

        // Save in background thread
        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    System.out.println("Attempting to save record...");
                    System.out.println("Student: " + studentName + ", ID: " + studentId);
                    System.out.println("Courses count: " + courses.size());
                    boolean result = DatabaseHelper.insertStudentRecord(record, courses);
                    System.out.println("Save result: " + result);
                    return result;
                } catch (Exception e) {
                    System.err.println("Exception in save task: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                Boolean success = getValue();
                if (success) {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Record saved successfully!\nStudent: " + studentName + "\nCGPA: " + String.format("%.2f", gpa));
                        saveRecordButton.setDisable(true);
                        studentNameField.setDisable(true);
                        studentIdField.setDisable(true);
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save record to database. Check console for details.")
                    );
                }
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                System.err.println("Task failed with exception: " + ex.getMessage());
                ex.printStackTrace();
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while saving the record.\n" + ex.getMessage())
                );
            }
        };

        // Run the task in background
        new Thread(saveTask).start();
    }

    @FXML
    protected void onBackToCourseEntry() {
        navigateToCourseEntry();
    }

    @FXML
    protected void onBackToHome() {
        navigateToHome();
    }

    private void navigateToCourseEntry() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("entry.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);

            // Pass the course data back to preserve state
            CourseEntryController controller = fxmlLoader.getController();
            controller.restoreState(courses, totalCreditsLabel.getText());

            Stage stage = (Stage) gpaValueLabel.getScene().getWindow();
            stage.setTitle("GPA Calculator - Course Entry");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 1000);

            Stage stage = (Stage) gpaValueLabel.getScene().getWindow();
            stage.setTitle("GPA Calculator - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
