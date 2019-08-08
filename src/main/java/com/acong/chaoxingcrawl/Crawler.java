package com.acong.chaoxingcrawl;

import com.acong.chaoxingcrawl.service.PostService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *  入口类
 */
public class Crawler extends Application {
    public static void main(String[] args){
        launch(args);

        new PostService().start();
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        primaryStage.setTitle("登陆");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Math.class.getResource("/css/jfoenix-components.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }
}
