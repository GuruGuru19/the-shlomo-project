package me.guruguru19.trajectorygraphing.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.guruguru19.trajectorygraphing.Main;
import me.guruguru19.trajectorygraphing.ui.controllers.AppController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class App extends Application {

    public void startApp(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Trajectory Graph");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/img/icon.png"));
        stage.setOnCloseRequest(e -> System.exit(0));


        FXMLLoader loader = new FXMLLoader(getURL("/gui/app.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root, 1920,1080));
        stage.show();
        System.out.println("111");
        Main.vision(((AppController)loader.getController()).getCameraFrame());
    }

    private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]:.$");

    public static URL getURL(String url) {
        try {
            if (!URL_QUICKMATCH.matcher(url).matches()) {
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                URL resource;
                if (url.charAt(0) == '/') {
                    resource = contextClassLoader.getResource(url.substring(1));
                } else {
                    resource = contextClassLoader.getResource(url);
                }

                if (resource == null) {
                    throw new IllegalArgumentException("Invalid URL or resource not found");
                } else {
                    return resource;
                }
            } else {
                return new URL(url);
            }
        } catch (MalformedURLException | IllegalArgumentException var3) {
            var3.printStackTrace();
            return null;
        }
    }
}
