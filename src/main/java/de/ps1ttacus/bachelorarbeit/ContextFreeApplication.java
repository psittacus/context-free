package de.ps1ttacus.bachelorarbeit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class ContextFreeApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root);

        stage.setMaximized(true);
        stage.setTitle("context-free");
        stage.setScene(scene);
        stage.show();
        Platform.runLater( () -> root.requestFocus() );
    }

    public static void main(String[] args) {
        launch();
    }


}