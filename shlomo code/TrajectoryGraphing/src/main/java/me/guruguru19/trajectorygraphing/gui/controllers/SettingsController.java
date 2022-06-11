package me.guruguru19.trajectorygraphing.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.guruguru19.trajectorygraphing.trajectory.TrajectoryCalc;
import me.guruguru19.trajectorygraphing.vision.TargetDetection;

public class SettingsController {

    @FXML
    private TextField ballDiameterBox;

    @FXML
    private TextField cameraMountingAngleBox;

    @FXML
    private TextField cameraMountingHightBox;

    @FXML
    private TextField camera_FOV_Y_Box;

    @FXML
    private TextField oneOverDtBox;

    @FXML
    private CheckBox isBallBox;

    @FXML
    private TextField projectileMassBox;

    @FXML
    private TextField targetAreaBox;

    @FXML
    private TextField targetDistanceBox;

    @FXML
    private TextField targetHightBox;

    @FXML
    private TextField densityOfFluidBox;

    @FXML
    private TextField projectileCrossSectionalAreaBox;

    @FXML
    private TextField dragCoefficientBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField hue_high_Box;

    @FXML
    private TextField hue_low_Box;

    @FXML
    private TextField saturation_high_Box;

    @FXML
    private TextField saturation_low_Box;

    @FXML
    private TextField value_high_Box;

    @FXML
    private TextField value_low_Box;

    @FXML
    private void isBallBoxChanged(ActionEvent e) {
        boolean q = isBallBox.isSelected();
        ballDiameterBox.setDisable(!q);
        projectileCrossSectionalAreaBox.setDisable(q);
        dragCoefficientBox.setDisable(q);
    }

    @FXML
    private void checkSave(ActionEvent e){
        saveButton.setDisable(!(targetDistanceBox.getText() != "" || targetHightBox.getText() != "" || targetAreaBox.getText() != "" ));
    }

    @FXML
    private void saveButtonPressed(ActionEvent e){
        TrajectoryCalc.setDisplay_ballDiameter(ballDiameterBox.getText());
        TrajectoryCalc.setDisplay_crossSectionalArea(projectileCrossSectionalAreaBox.getText());
        TrajectoryCalc.setDisplay_Cd(dragCoefficientBox.getText());

        double mountingAngle = 0;
        if (cameraMountingAngleBox.getText() != ""){
            mountingAngle = Double.parseDouble(cameraMountingAngleBox.getText());
        }
        double mountingHight = 0;
        if (cameraMountingHightBox.getText() != ""){
            mountingHight = Double.parseDouble(cameraMountingHightBox.getText());
        }
        if (camera_FOV_Y_Box.getText() != ""){
            TargetDetection.setCameraFovY(Integer.parseInt(camera_FOV_Y_Box.getText()));
        }

        double targetHight = 0;
        if (targetHightBox.getText() != ""){
            targetHight = Double.parseDouble(targetHightBox.getText());
        }
        double targetDistance = -1;
        if (targetDistanceBox.getText() != ""){
            targetDistance = Double.parseDouble(targetDistanceBox.getText());
        }
        double targetArea = 0;
        if (targetAreaBox.getText() != ""){
            targetArea = Double.parseDouble(targetAreaBox.getText());
        }

        double dt = 0.005;
        if (oneOverDtBox.getText() != ""){
            dt = 1/Double.parseDouble(oneOverDtBox.getText());
        }

        double projectileMass = 0;
        if (projectileMassBox.getText() != ""){
            projectileMass = Double.parseDouble(projectileMassBox.getText());
        }

        double densityOfFluid = 1.2;
        if (densityOfFluidBox.getText() != ""){
            densityOfFluid = Double.parseDouble(densityOfFluidBox.getText());
        }

        double dragCoefficient = 0;
        if (isBallBox.isSelected()){
            double ballDiameter = 0;
            if (ballDiameterBox.getText() != ""){
                ballDiameter = Double.parseDouble(ballDiameterBox.getText());
            }
            double r = ballDiameter/2.0;
            dragCoefficient = TrajectoryCalc.dragCoefficientCalc(Math.PI*r*r,0.47, densityOfFluid);
        }
        else {
            double crossSectionalArea = 0;
            if (projectileCrossSectionalAreaBox.getText() != ""){
                crossSectionalArea = Double.parseDouble(projectileCrossSectionalAreaBox.getText());
            }
            double Cd = 0;
            if (dragCoefficientBox.getText() != ""){
                Cd = Double.parseDouble(dragCoefficientBox.getText());
            }
            dragCoefficient = TrajectoryCalc.dragCoefficientCalc(crossSectionalArea, Cd, densityOfFluid);
        }

        TrajectoryCalc.CalcOperations operation = null;
        if (targetDistanceBox.getText() != "" && targetHightBox.getText() != ""){
            operation = TrajectoryCalc.CalcOperations.SET_TARGET;
        }
        else if (targetDistanceBox.getText() == "" && targetHightBox.getText() != ""){
            operation = TrajectoryCalc.CalcOperations.SET_TARGET_HIGHT;
        }
        else if (targetAreaBox.getText() != "") {
            operation = TrajectoryCalc.CalcOperations.SET_TARGET_AREA;
        }

        TrajectoryCalc.setInitialState(mountingAngle, mountingHight, dragCoefficient, projectileMass, dt, operation, targetHight, targetDistance, targetArea);

        if (hue_high_Box.getText().isEmpty()||hue_low_Box.getText().isEmpty()||saturation_high_Box.getText().isEmpty()||saturation_low_Box.getText().isEmpty()||value_high_Box.getText().isEmpty()||value_low_Box.getText().isEmpty()){
            System.out.println("color not updated");
        }
        else {
            try{
                int hh = Integer.parseInt(hue_high_Box.getText());
                int hl = Integer.parseInt(hue_low_Box.getText());
                int sh = Integer.parseInt(saturation_high_Box.getText());
                int sl = Integer.parseInt(saturation_low_Box.getText());
                int vh = Integer.parseInt(value_high_Box.getText());
                int vl = Integer.parseInt(value_low_Box.getText());
                TargetDetection.setColor(hh, hl, sh, sl, vh, vl);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        window.close();
    }

    @FXML
    void exitButtonPressed(ActionEvent e) {
        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        window.close();
    }

    public void init(){
        ballDiameterBox.setText(TrajectoryCalc.getDisplay_ballDiameter());
        projectileCrossSectionalAreaBox.setText(TrajectoryCalc.getDisplay_crossSectionalArea());
        dragCoefficientBox.setText(TrajectoryCalc.getDisplay_Cd());

        if (TrajectoryCalc.getCameraAngle() != 0){
            cameraMountingAngleBox.setText(String.valueOf(TrajectoryCalc.getCameraAngle()));
        }
        if (TrajectoryCalc.getCameraHight() != 0){
            cameraMountingHightBox.setText(String.valueOf(TrajectoryCalc.getCameraHight()));
        }

        if (TrajectoryCalc.getTargetDistance() != -1){
            targetDistanceBox.setText(String.valueOf(TrajectoryCalc.getTargetDistance()));
        }
        if (TrajectoryCalc.getTargetArea() != 0){
            targetAreaBox.setText(String.valueOf(TrajectoryCalc.getTargetArea()));
        }
        if (TrajectoryCalc.getTargetHight() != 0){
            targetHightBox.setText(String.valueOf(TrajectoryCalc.getTargetHight()));
        }

        if (TrajectoryCalc.getProjectileMass() != 0){
            projectileMassBox.setText(String.valueOf(TrajectoryCalc.getProjectileMass()));
        }


        //camera_FOV_Y_Box.setText();

    }
}
