package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditRecordController {

    @FXML
    private TextField studentNameField;

    @FXML
    private TextField studentIdField;

    @FXML
    private Label cgpaLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalCreditsLabel;

    private StudentRecord record;
    private RecordsViewController recordsViewController;

    public void setStudentRecord(StudentRecord record) {
        this.record = record;
        populateFields();
    }

    public void setRecordsViewController(RecordsViewController controller) {
        this.recordsViewController = controller;
    }

    private void populateFields() {
        studentNameField.setText(record.getStudentName());
        studentIdField.setText(record.getStudentId());
        cgpaLabel.setText(String.format("%.2f", record.getCgpa()));
        totalCoursesLabel.setText(String.valueOf(record.getTotalCourses()));
        totalCreditsLabel.setText(String.format("%.1f", record.getTotalCredits()));
    }

    @FXML
    protected void onUpdate() {
        String studentName = studentNameField.getText().trim();
        String studentId = studentIdField.getText().trim();

        // Validate inputs
        if (studentName.isEmpty() || studentId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information",
                    "Please enter both student name and student ID.");
            return;
        }

        // Update record
        record.setStudentName(studentName);
        record.setStudentId(studentId);

        // Save in background thread
        Task<Boolean> updateTask = new Task<>() {
            @Override
            protected Boolean call() {
                return DatabaseHelper.updateStudentRecord(record);
            }

            @Override
            protected void succeeded() {
                Boolean success = getValue();
                Platform.runLater(() -> {
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Record updated successfully!");
                        navigateBackToRecords();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Failed to update record in database.");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "An error occurred while updating the record.")
                );
            }
        };

        new Thread(updateTask).start();
    }

    @FXML
    protected void onCancel() {
        navigateBackToRecords();
    }

    private void navigateBackToRecords() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("recordsview.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

            Stage stage = (Stage) studentNameField.getScene().getWindow();
            stage.setTitle("CGPA Calculator - Student Records");
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
