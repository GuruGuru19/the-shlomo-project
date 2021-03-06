package me.guruguru19.trajectorygraphing.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import me.guruguru19.trajectorygraphing.gui.SettingsWindow;
import me.guruguru19.trajectorygraphing.trajectory.GraphDrawer;
import me.guruguru19.trajectorygraphing.trajectory.LaunchPlan;
import me.guruguru19.trajectorygraphing.trajectory.Target;
import me.guruguru19.trajectorygraphing.trajectory.TrajectoryCalc;

import java.io.IOException;

public class AppController {
    @FXML
    public Button settingsButton;

    @FXML
    private Button snapButton;

    @FXML
    private Button clearButton;

    @FXML
    private ImageView cameraFrame;

    @FXML
    private ImageView snapFrame;

    @FXML
    private ImageView thresholdFrame;

    @FXML
    private LineChart<?, ?> graphChart;

    @FXML
    private AnchorPane mainPane;

    public ImageView getCameraFrame(){
        return this.cameraFrame;
    }
    public ImageView getSnapFrame(){
        return this.snapFrame;
    }
    public ImageView getThresholdFrame(){
        return this.thresholdFrame;
    }

    /**
     * opening the settings window
     * @param event
     */
    @FXML
    private void settingsButtonPressed(ActionEvent event){

        Platform.runLater(() -> {
            try {
                SettingsWindow.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * starting the trajectory calculating and graphing
     * @param event
     */
    @FXML
    private void snapButtonPressed(ActionEvent event){
        GraphDrawer.snap(snapFrame);
        Target target = GraphDrawer.getTarget();
        LaunchPlan plan = TrajectoryCalc.calc(target.getMx(), target.getMy(), target.getMa());


        XYChart.Series series = new XYChart.Series<>();
        series.setName("trajectory");
        GraphDrawer.draw(plan, series);
        //series.getData().add(new XYChart.Data<>(1,1));
        graphChart.getData().add(series);
    }

    /**
     * clearing the graphs on the chart
     * @param event
     */
    @FXML
    void clearButtonPressed(ActionEvent event) {
        graphChart.getData().clear();
    }
}
