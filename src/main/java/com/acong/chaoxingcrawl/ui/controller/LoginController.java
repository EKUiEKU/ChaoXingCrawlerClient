package com.acong.chaoxingcrawl.ui.controller;

import com.acong.chaoxingcrawl.utils.PropertiesUtil;
import com.acong.chaoxingcrawl.utils.net.UserUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.acong.chaoxingcrawl.interfaces.OnLoginListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable, EventHandler<ActionEvent> , OnLoginListener {

    @FXML
    private JFXTextField tf_username;
    @FXML
    private JFXPasswordField tf_password;
    @FXML
    private JFXCheckBox cb_rememberMe;
    @FXML
    private JFXButton btn_login;
    @FXML
    private Hyperlink hyp_register;
    @FXML
    private Text tv_info;

    public void initialize(URL location, ResourceBundle resources) {
        btn_login.setOnAction(this);
        hyp_register.setOnAction(this);

        /**
         * 加载储存的账号和密码
         */
        PropertiesUtil util = new PropertiesUtil();
        Properties properties = util.getProperties();
        if (properties != null){
            String rememberMe = properties.getProperty("rememberMe");

            if (rememberMe.equals("true")){
                cb_rememberMe.setSelected(true);
                //读取账号和密码
                String username = properties.getProperty("username");
                String password = properties.getProperty("password");

                tf_username.setText(username);
                tf_password.setText(password);
            }
        }
    }

    public void handle(ActionEvent event) {
        String who = event.getSource().toString();
        if (who.contains("btn_login")){
            final String username = tf_username.getText().trim();
            final String password = tf_password.getText().trim();
            Boolean isCheck = cb_rememberMe.isSelected();

            if (username.equals("") || password.equals("")){
                tv_info.setText("账号或密码不能为空");
                return;
            }

            btn_login.setDisable(true);
            tf_username.setDisable(true);
            tf_password.setDisable(true);
            cb_rememberMe.setDisable(true);

            /**
             * 启动线程 防止UI卡顿。
             */
            new Thread(new Runnable() {
                public void run() {
                    UserUtil.create()
                            .login(username,password,LoginController.this);
                }
            }).start();
        }else if(who.contains("hyp_register")){
            try {
                Runtime.getRuntime().exec("cmd /c start http://47.102.130.244/reg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadShuake(final Long uid){
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    BorderPane page = FXMLLoader.load(getClass().getResource("/fxml/shuake.fxml"));
                    Scene newScene = new Scene(page,1000,600);
                    Stage stage = new Stage();
                    newScene.getStylesheets().add(Math.class.getResource("/css/jfoenix-components.css").toExternalForm());
                    stage.setScene(newScene);
                    stage.getScene().setUserData(uid);
                    stage.show();

                    shoutdown();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void shoutdown(){
        btn_login.getScene().getWindow().hide();
        relaseMenory();
    }

    private void relaseMenory(){
        btn_login = null;
        tf_username = null;
        tf_password = null;
        cb_rememberMe = null;
        hyp_register = null;
        tv_info = null;
        System.gc();
    }

    public void onLoginSuccess(Long uid) {
        loadShuake(uid);

        /**
         * 保存账号和密码
         */
        if (cb_rememberMe.isSelected() == true){
            PropertiesUtil util = new PropertiesUtil();
            util.writeProperty("username",tf_username.getText().trim());
            util.writeProperty("password",tf_password.getText().trim());
            util.writeProperty("rememberMe","true");
        }
    }

    public void onLoginFailure(String causeBy) {
        btn_login.setDisable(false);
        tf_username.setDisable(false);
        tf_password.setDisable(false);
        cb_rememberMe.setDisable(false);

        tv_info.setText(causeBy);
    }

}
