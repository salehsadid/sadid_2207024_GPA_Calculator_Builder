package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ViewDetailsController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label studentIdLabel;

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
    private Label recordDateLabel;

    @FXML
    private VBox courseTableBody;

    private StudentRecord record;

    public void setStudentRecord(StudentRecord record) {
        this.record = record;
        displayRecordDetails();
    }

    private void displayRecordDetails() {
        // Set student info
        studentNameLabel.setText(record.getStudentName());
        studentIdLabel.setText("ID: " + record.getStudentId());

        // Set GPA
        gpaValueLabel.setText(String.format("%.2f", record.getCgpa()));

        // Set performance label
        String performance = getPerformanceLevel(record.getCgpa());
        gpaPerformanceLabel.setText(performance);
        gpaPerformanceLabel.setStyle(getPerformanceColor(record.getCgpa()));

        // Set summary
        totalCoursesLabel.setText(String.valueOf(record.getTotalCourses()));
        totalCreditsLabel.setText(String.format("%.1f", record.getTotalCredits()));
        averageGradeLabel.setText(record.getAverageGrade());
        recordDateLabel.setText(record.getRecordDate());

        // Display course table
        displayCourseTable();
    }

    private void displayCourseTable() {
        courseTableBody.getChildren().clear();

        List<Course> courses = record.getCourses();
        if (courses != null) {
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                HBox row = createTableRow(i + 1, course);
                courseTableBody.getChildren().add(row);
            }
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

    @FXML
    protected void onBackToRecords() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recordsview.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

            Stage stage = (Stage) studentNameLabel.getScene().getWindow();
            stage.setTitle("CGPA Calculator - Student Records");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
