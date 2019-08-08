package com.acong.chaoxingcrawl.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShuakeView extends Application {
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/shuake.fxml"));
        primaryStage.setTitle("刷课");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Math.class.getResource("/css/jfoenix-components.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
