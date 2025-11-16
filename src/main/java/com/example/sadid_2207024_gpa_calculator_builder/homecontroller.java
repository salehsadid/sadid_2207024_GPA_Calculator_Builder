package com.example.sadid_2207024_gpa_calculator_builder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
public class homecontroller {

    @FXML
    private Button startButton;

    @FXML
    protected void onStartButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("entry.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setTitle(" CGPA Calculator - Course Entry");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
