package me.guruguru19.trajectorygraphing.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    private TextField ball_diameter_textField;


    @FXML
    private void saveButton(ActionEvent e){
        //TODO: save data

        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
        window.close();
    }
}
