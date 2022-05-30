package me.guruguru19.trajectorygraphing.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import me.guruguru19.trajectorygraphing.ui.SettingsWindow;

import java.awt.*;
import java.io.IOException;

public class AppController {
    @FXML
    public Button settingsButton;
    @FXML
    private ImageView cameraFrame;

    @FXML
    private LineChart<?, ?> graphChart;

    @FXML
    private AnchorPane mainPane;

    public ImageView getCameraFrame(){
        return this.cameraFrame;
    }

    @FXML
    private void settingsButtonPressed(){

        Platform.runLater(() -> {
            try {
                SettingsWindow.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
