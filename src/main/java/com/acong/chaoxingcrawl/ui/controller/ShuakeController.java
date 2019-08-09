package com.acong.chaoxingcrawl.ui.controller;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean.*;
import com.acong.chaoxingcrawl.mq.Looper;
import com.acong.chaoxingcrawl.taskes.WatchChaoXingTask;
import com.acong.chaoxingcrawl.utils.PropertiesUtil;
import com.acong.chaoxingcrawl.utils.UserUtil;
import com.acong.chaoxingcrawl.utils.interfaces.OnUploadClassesListener;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import com.acong.chaoxingcrawl.bean.Class;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ShuakeController implements Initializable, EventHandler<ActionEvent>, ChaoXingTaskExecutor.OnMessageQueueListener, UserUtil.OnUploadInfoListener, OnUploadClassesListener {
    @FXML
    private JFXButton btn_login;
    @FXML
    private JFXTextField tf_username;
    @FXML
    private JFXPasswordField tf_password;
    @FXML
    private JFXTextArea ta_console;
    @FXML
    private JFXListView<Label> lv_clazz;
    @FXML
    private JFXTextField tf_unit;
    @FXML
    private JFXTextField tf_course;
    @FXML
    private Text text_course;
    @FXML
    private Text text_totalTime;
    @FXML
    private Text text_course_progress;
    @FXML
    private Text text_class_name;
    @FXML
    private Text text_type;
    @FXML
    private Text text_class_progress;


    private ChaoXingTaskExecutor chaoXingTaskExecutor;
    private Thread thread;

    private Long uid;
    private List<Class> classes;

    public void handle(ActionEvent event) {
        String who = event.getSource().toString();

        if (who.contains("btn_login")) {
            start();
        }
    }

    private void start() {

        uid = (Long) btn_login.getScene().getUserData();

        String username = tf_username.getText().trim();
        String password = tf_password.getText().trim();



        tf_course.setDisable(true);
        tf_unit.setDisable(true);
        tf_username.setDisable(true);
        tf_password.setDisable(true);
        btn_login.setDisable(true);

        final UserInfo info = new UserInfo();
        info.setUsername(username);
        info.setPassword(password);
        info.setCourseName(tf_course.getText().trim());

        thread = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                chaoXingTaskExecutor = ChaoXingTaskExecutor.getInstance();
                chaoXingTaskExecutor.setOnMessageQueueListener(ShuakeController.this);
                chaoXingTaskExecutor.execute(new WatchChaoXingTask(info));
                Looper.loop();
            }
        });
        thread.start();
    }

    public void initialize(URL location, ResourceBundle resources) {
        btn_login.setOnAction(this);

        /**
         * 加载相关信息
         */
        Properties p = new PropertiesUtil().getProperties();
        if (p != null){
            tf_unit.setText(p.getProperty("unit"));
            tf_course.setText(p.getProperty("course"));
            tf_username.setText(p.getProperty("chaoxing.username"));
            tf_password.setText(p.getProperty("chaoxing.password"));
        }
    }

    public void onMessage(String msg) {
        ta_console.appendText(msg + "\n");
    }

    private int completed = 0;
    private int total = 0;
    public void onClassInfo(ClazzBean info) {
        Label label = new Label(info.getClassName());
        if (info.getComplete()){
            label.setTextFill(Color.rgb(111,255,22));
            completed++;
        }else{
            label.setTextFill(Color.rgb(255,55,33));
        }
        lv_clazz.getItems().add(label);

        text_totalTime.setText(++total + "学时");
        text_course_progress.setText(completed + "/" + total +  "(" +Math.round(completed * 1.0 / total * 100.) + "%)");

        Class c = new Class();
        c.setCompleted(info.getComplete());
        c.setTitle(info.getClassName());
        classes.add(c);
    }

    public void onLoginFailure() {
        tf_username.setDisable(false);
        tf_password.setDisable(false);
        btn_login.setDisable(false);
        tf_course.setDisable(false);
        tf_unit.setDisable(false);
    }

    public void onLoginSuccess(String name) {
        _UserInfo info = new _UserInfo();
        info.setSchool(tf_unit.getText());
        info.setUid(uid);
        info.setName(name);
        info.setLastTime(System.currentTimeMillis());
        info.setStudentid(tf_username.getText());

        UserUtil.create()
                .uploadInfo(info,this);

        classes = new ArrayList<Class>();

        /**
         * 在本地保存相关信息
         */
        PropertiesUtil util = new PropertiesUtil();
        util.writeProperty("unit",tf_unit.getText());
        util.writeProperty("course",tf_course.getText());
        util.writeProperty("chaoxing.username",tf_username.getText());
        util.writeProperty("chaoxing.password",tf_password.getText());
    }

    public void onCourseName(String courseName) {
        text_course.setText(courseName);
    }

    public void onCourseNoFound(String courseName) {
        tf_username.setDisable(false);
        tf_password.setDisable(false);
        btn_login.setDisable(false);
        tf_course.setDisable(false);
        tf_unit.setDisable(false);
    }

    public void onStartClass(String className) {
        text_class_name.setText(className);
    }

    public void onPlaying(String type) {
        text_type.setText(type);
    }

    public void onPlayingVideoProgress(Progress p) {
        text_class_progress.setText(p.getCurrentTime() + "/" + p.getTotalTime());
    }

    public void onPlayingPPTProgress(String pageAt) {
        text_class_progress.setText("正在放映:" + pageAt + "页");
    }

    public void onExceptiom() {
        tf_username.setDisable(false);
        tf_password.setDisable(false);
        btn_login.setDisable(false);
        tf_course.setDisable(false);
        tf_unit.setDisable(false);
    }

    public void onCourseStart() {
        /**
         * 将课程信息上传至云端。
         */
        Classes c = new Classes();
        c.setCourseTitle(tf_course.getText());
        c.setUid(uid);
        c.setUpdateTime(System.currentTimeMillis());
        c.setUsername(tf_username.getText());
        c.setClasses(classes);
        c.setUnit(tf_unit.getText());
        UserUtil.create()
                .uploadClassesListener(c,this);
    }

    public void onUploadSuccess() {
    }

    public void onUploadFailure(String causeBy) {

    }

    public void uploadSuccess() {

    }

    public void uploadFailure(String causeBy) {

    }
}
