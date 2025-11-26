package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CourseEntryController implements Initializable {

    @FXML
    private TextField totalCreditsField;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField courseCodeField;

    @FXML
    private TextField courseCreditField;

    @FXML
    private TextField teacher1Field;

    @FXML
    private TextField teacher2Field;

    @FXML
    private ComboBox<String> gradeComboBox;   //dropdown for grades

    @FXML
    private VBox courseListContainer; // list of courses display korar jonno

    @FXML
    private Button calculateGPAButton;

    @FXML
    private Label creditsStatusLabel;

    private List<Course> courses = new ArrayList<>();
    private double totalCreditsRequired = 0.0;
    private double currentCreditsEntered = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gradeComboBox.getItems().addAll(GpaCalc.getAvailableGrades());

        courseListContainer.getChildren().add(createEmptyListLabel());
    }

    @FXML
    protected void onSetTotalCredits() {
        try {
            String input = totalCreditsField.getText().trim();
            if (input.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter total credits.");
                return;
            }

            double credits = Double.parseDouble(input);
            if (credits <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Total credits must be greater than 0.");
                return;
            }

            totalCreditsRequired = credits;
            updateCreditsStatus();
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Total credits required set to: " + totalCreditsRequired);
            totalCreditsField.setDisable(true);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for credits.");
        }
    }

    @FXML
    protected void onAddCourse() {
        // Validate inputs
        if (totalCreditsRequired == 0) {
            showAlert(Alert.AlertType.WARNING, "Missing Information",
                    "Please set the total credits required first.");
            return;
        }

        String courseName = courseNameField.getText().trim();
        String courseCode = courseCodeField.getText().trim();
        String creditText = courseCreditField.getText().trim();
        String teacher1 = teacher1Field.getText().trim();
        String teacher2 = teacher2Field.getText().trim();
        String grade = gradeComboBox.getValue();

        // Validation
        if (courseName.isEmpty() || courseCode.isEmpty() || creditText.isEmpty() ||
                teacher1.isEmpty() || teacher2.isEmpty() || grade == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information",
                    "Please fill in all fields before adding a course.");
            return;
        }

        try {
            double credit = Double.parseDouble(creditText);
            if (credit <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Credit",
                        "Course credit must be greater than 0.");
                return;
            }

            // Check if adding this course would exceed total credits
            if (currentCreditsEntered + credit > totalCreditsRequired) {
                showAlert(Alert.AlertType.WARNING, "Credit Limit Exceeded",
                        String.format("Adding this course (%.1f credits) would exceed the total required credits (%.1f).\nRemaining credits: %.1f",
                                credit, totalCreditsRequired, totalCreditsRequired - currentCreditsEntered));
                return;
            }

            // Create and add course
            Course course = new Course(courseName, courseCode, credit, teacher1, teacher2, grade);
            courses.add(course);
            currentCreditsEntered += credit;

            // Update UI
            updateCourseList();
            clearForm();
            updateCreditsStatus();

            // Enable Calculate GPA button if credits match
            if (Math.abs(currentCreditsEntered - totalCreditsRequired) < 0.01) {
                calculateGPAButton.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "Ready to Calculate",
                        "You have entered all required credits! You can now calculate your GPA.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Course Added",
                        "Course added successfully!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Credit",
                    "Please enter a valid number for course credit.");
        }
    }

    @FXML
    protected void onClearForm() {
        clearForm();
    }

    @FXML
    protected void onCalculateGPA() {
        if (courses.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Courses", "Please add courses before calculating GPA.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resultpage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);

            GpaResultController controller = fxmlLoader.getController();
            controller.setCoursesData(courses, totalCreditsRequired);

            Stage stage = (Stage) calculateGPAButton.getScene().getWindow();
            stage.setTitle("GPA Calculator - Results");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load GPA result page.");
        }
    }

    @FXML
    protected void onBackToHome() {
        if (!courses.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Navigation");
            alert.setHeaderText("Are you sure you want to go back?");
            alert.setContentText("All entered course data will be lost.");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }

        navigateToHome();
    }

    @FXML
    protected void onResetAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Reset");
        alert.setHeaderText("Are you sure you want to reset all data?");
        alert.setContentText("This will clear all courses and reset the total credits.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            courses.clear();
            currentCreditsEntered = 0.0;
            totalCreditsRequired = 0.0;
            totalCreditsField.clear();
            totalCreditsField.setDisable(false);
            clearForm();
            updateCourseList();
            updateCreditsStatus();
            calculateGPAButton.setDisable(true);
        }
    }

    private void clearForm() {
        courseNameField.clear();
        courseCodeField.clear();
        courseCreditField.clear();
        teacher1Field.clear();
        teacher2Field.clear();
        gradeComboBox.setValue(null);
    }

    private void updateCourseList() {
        courseListContainer.getChildren().clear();

        if (courses.isEmpty()) {
            courseListContainer.getChildren().add(createEmptyListLabel());
        } else {
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                courseListContainer.getChildren().add(createCourseCard(course, i));
            }
        }
    }

    private Label createEmptyListLabel() {
        Label label = new Label("No courses added yet. Add your first course above.");
        label.setFont(new Font("Arial", 13));
        label.setStyle("-fx-text-fill: #666666; -fx-padding: 20;");
        return label;
    }

    private VBox createCourseCard(Course course, int index) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 15; -fx-background-radius: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label titleLabel = new Label((index + 1) + ". " + course.getCourseName() + " (" + course.getCourseCode() + ")");
        titleLabel.setFont(new Font("Arial Bold", 14));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label detailsLabel = new Label(String.format("Credit: %.1f | Grade: %s | Teachers: %s, %s",
                course.getCredit(), course.getGrade(), course.getTeacher1(), course.getTeacher2()));
        detailsLabel.setFont(new Font("Arial", 12));
        detailsLabel.setStyle("-fx-text-fill: #555555;");

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        deleteButton.setOnAction(e -> deleteCourse(index));

        actionBox.getChildren().add(deleteButton);

        card.getChildren().addAll(titleLabel, detailsLabel, actionBox);

        return card;
    }

    private void deleteCourse(int index) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Course");
        alert.setContentText("Are you sure you want to delete this course?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Course course = courses.remove(index);
            currentCreditsEntered -= course.getCredit();
            updateCourseList();
            updateCreditsStatus();

            if (Math.abs(currentCreditsEntered - totalCreditsRequired) >= 0.01) {
                calculateGPAButton.setDisable(true);
            }
        }
    }

    private void updateCreditsStatus() {
        if (totalCreditsRequired > 0) {
            creditsStatusLabel.setText(String.format("Credits Entered: %.1f / %.1f",
                    currentCreditsEntered, totalCreditsRequired));

            if (Math.abs(currentCreditsEntered - totalCreditsRequired) < 0.01) {
                creditsStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                creditsStatusLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
            }
        } else {
            creditsStatusLabel.setText("");
        }
    }

    private void navigateToHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);

            Stage stage = (Stage) calculateGPAButton.getScene().getWindow();
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

    public void restoreState(List<Course> coursesList, String totalCreditsText) {
        // Restore courses
        this.courses = new ArrayList<>(coursesList);

        // Parse and restore total credits
        try {
            this.totalCreditsRequired = Double.parseDouble(totalCreditsText);
            this.totalCreditsField.setText(String.valueOf(this.totalCreditsRequired));
            this.totalCreditsField.setDisable(true);

            // Calculate current credits
            this.currentCreditsEntered = 0.0;
            for (Course course : courses) {
                this.currentCreditsEntered += course.getCredit();
            }

            // Update UI
            updateCourseList();
            updateCreditsStatus();

            // Enable Calculate button if credits match
            if (Math.abs(currentCreditsEntered - totalCreditsRequired) < 0.01) {
                calculateGPAButton.setDisable(false);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, just restore the courses
            updateCourseList();
        }
    }
}
