package me.guruguru19.trajectorygraphing.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.guruguru19.trajectorygraphing.gui.controllers.SettingsController;

import java.io.IOException;

public class SettingsWindow {
    public static void init() throws IOException {
        Stage stage = new Stage();

        stage.setTitle("settings");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/img/icon.png"));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader(App.getURL("/gui/settings.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root, 1280,720));
        SettingsController settingsController = (SettingsController)loader.getController();
        settingsController.init();
        stage.show();
    }
}
