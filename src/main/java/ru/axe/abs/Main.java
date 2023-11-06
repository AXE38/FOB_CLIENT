package ru.axe.abs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(MainWindow.class.getResourceAsStream("MainWindow.fxml"));
        final MainWindow controller = loader.getController();
        controller.setStage(stage);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> controller.onStart());
        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("АБС Фронт-офис версия 05.2022");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}