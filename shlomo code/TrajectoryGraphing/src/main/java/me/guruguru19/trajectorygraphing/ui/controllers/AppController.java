package me.guruguru19.trajectorygraphing.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.AnchorPane;
import me.guruguru19.trajectorygraphing.ui.SettingsWindow;

import java.io.IOException;

public class AppController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private LineChart graphChart;

    @FXML
    private void settingsButton(){

        Platform.runLater(() -> {
            try {
                SettingsWindow.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
