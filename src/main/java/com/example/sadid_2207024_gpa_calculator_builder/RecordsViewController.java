package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecordsViewController implements Initializable {

    @FXML
    private TableView<StudentRecord> recordsTable;

    @FXML
    private TableColumn<StudentRecord, Integer> recordIdColumn;

    @FXML
    private TableColumn<StudentRecord, String> studentNameColumn;

    @FXML
    private TableColumn<StudentRecord, String> studentIdColumn;

    @FXML
    private TableColumn<StudentRecord, Integer> totalCoursesColumn;

    @FXML
    private TableColumn<StudentRecord, Double> totalCreditsColumn;

    @FXML
    private TableColumn<StudentRecord, Double> cgpaColumn;

    @FXML
    private TableColumn<StudentRecord, String> averageGradeColumn;

    @FXML
    private TableColumn<StudentRecord, String> recordDateColumn;

    @FXML
    private TableColumn<StudentRecord, Void> actionsColumn;

    @FXML
    private VBox loadingPane;

    @FXML
    private VBox emptyPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadRecords();
    }

    private void setupTableColumns() {
        recordIdColumn.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        totalCoursesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCourses"));
        totalCreditsColumn.setCellValueFactory(new PropertyValueFactory<>("totalCredits"));
        cgpaColumn.setCellValueFactory(new PropertyValueFactory<>("cgpa"));
        averageGradeColumn.setCellValueFactory(new PropertyValueFactory<>("averageGrade"));
        recordDateColumn.setCellValueFactory(new PropertyValueFactory<>("recordDate"));

        // Format CGPA column
        cgpaColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double cgpa, boolean empty) {
                super.updateItem(cgpa, empty);
                if (empty || cgpa == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", cgpa));
                }
            }
        });

        // Format Credits column
        totalCreditsColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double credits, boolean empty) {
                super.updateItem(credits, empty);
                if (empty || credits == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", credits));
                }
            }
        });

        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("View");
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(8, viewButton, editButton, deleteButton);

            {
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                buttonBox.setAlignment(Pos.CENTER);

                viewButton.setOnAction(event -> {
                    StudentRecord record = getTableView().getItems().get(getIndex());
                    onViewRecord(record);
                });

                editButton.setOnAction(event -> {
                    StudentRecord record = getTableView().getItems().get(getIndex());
                    onEditRecord(record);
                });

                deleteButton.setOnAction(event -> {
                    StudentRecord record = getTableView().getItems().get(getIndex());
                    onDeleteRecord(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void loadRecords() {
        showLoading(true);

        Task<ObservableList<StudentRecord>> loadTask = new Task<>() {
            @Override
            protected ObservableList<StudentRecord> call() {
                return DatabaseHelper.getAllStudentRecords();
            }

            @Override
            protected void succeeded() {
                ObservableList<StudentRecord> records = getValue();
                Platform.runLater(() -> {
                    recordsTable.setItems(records);
                    showLoading(false);

                    if (records.isEmpty()) {
                        emptyPane.setVisible(true);
                    } else {
                        emptyPane.setVisible(false);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load records from database.");
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        recordsTable.setVisible(!show);
    }

    private void onViewRecord(StudentRecord record) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("viewdetails.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);

            ViewDetailsController controller = fxmlLoader.getController();

            // Load full record with courses
            StudentRecord fullRecord = DatabaseHelper.getStudentRecordById(record.getRecordId());
            controller.setStudentRecord(fullRecord);

            Stage stage = (Stage) recordsTable.getScene().getWindow();
            stage.setTitle("CGPA Calculator - Record Details");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open record details.");
        }
    }

    private void onEditRecord(StudentRecord record) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editrecord.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            EditRecordController controller = fxmlLoader.getController();
            controller.setStudentRecord(record);
            controller.setRecordsViewController(this);

            Stage stage = (Stage) recordsTable.getScene().getWindow();
            stage.setTitle("CGPA Calculator - Edit Record");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open edit dialog.");
        }
    }

    private void onDeleteRecord(StudentRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Student Record");
        alert.setContentText("Are you sure you want to delete the record for " + record.getStudentName() +
                " (ID: " + record.getStudentId() + ")?\nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteRecordInBackground(record);
        }
    }

    private void deleteRecordInBackground(StudentRecord record) {
        Task<Boolean> deleteTask = new Task<>() {
            @Override
            protected Boolean call() {
                return DatabaseHelper.deleteStudentRecord(record.getRecordId());
            }

            @Override
            protected void succeeded() {
                Boolean success = getValue();
                Platform.runLater(() -> {
                    if (success) {
                        recordsTable.getItems().remove(record);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Record deleted successfully.");

                        if (recordsTable.getItems().isEmpty()) {
                            emptyPane.setVisible(true);
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete record.");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the record.")
                );
            }
        };

        new Thread(deleteTask).start();
    }

    @FXML
    protected void onRefresh() {
        loadRecords();
    }

    @FXML
    protected void onBackToHome() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("home.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);

            Stage stage = (Stage) recordsTable.getScene().getWindow();
            stage.setTitle("CGPA Calculator - Home");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTable() {
        loadRecords();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
