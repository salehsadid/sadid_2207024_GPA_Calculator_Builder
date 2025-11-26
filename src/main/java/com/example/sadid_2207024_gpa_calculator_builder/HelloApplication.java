package com.example.sadid_2207024_gpa_calculator_builder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database
        DatabaseHelper.initializeDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setTitle("GPA Calculator - Home");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(800);
        stage.show();

        // Close database connection when application closes
        stage.setOnCloseRequest(event -> DatabaseHelper.closeConnection());
    }
}
