package com.acong.chaoxingcrawl.ui.controller;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean.Class;
import com.acong.chaoxingcrawl.bean.*;
import com.acong.chaoxingcrawl.mq.Looper;
import com.acong.chaoxingcrawl.taskes.WatchChaoXingTask;
import com.acong.chaoxingcrawl.utils.PropertiesUtil;
import com.acong.chaoxingcrawl.utils.net.UserUtil;
import com.acong.chaoxingcrawl.utils.interfaces.OnUploadClassesListener;
import com.jfoenix.controls.*;
import com.acong.chaoxingcrawl.interfaces.OnUploadInfoListener;
import com.acong.chaoxingcrawl.interfaces.impl.MessageQueueListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ShuakeController extends MessageQueueListener
        implements Initializable, EventHandler<ActionEvent>, OnUploadInfoListener, OnUploadClassesListener {
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
        info.setSchool(tf_unit.getText());

        lv_clazz.getItems().clear();
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


    /**
     * 上传用户数据 成功的回调
     */
    public void onUploadSuccess() {
    }

    /**
     * 上传用户数据 失败的回调
     */
    public void onUploadFailure(String causeBy) {

    }

    /**
     * 上传课表的数据 成功回调
     */
    public void uploadSuccess() {

    }

    /**
     * 上传课表的数据 失败回调
     */
    public void uploadFailure(String causeBy) {

    }


    //------------------------------线程池和UI交互的接口 ↓↓↓-------------------------
    private int completed = 0;
    private int total = 0;
    @Override
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

    @Override
    public void onLoginFailure() {
        enableInteractive(false);
    }

    private String name;

    @Override
    public void onLoginSuccess(String name) {
        this.name = name;

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

    @Override
    public void onCourseName(String courseName) {
        text_course.setText(courseName);
    }

    @Override
    public void onCourseNoFound(String courseName) {
        enableInteractive(false);
    }

    @Override
    public void onStartClass(String className) {
        text_class_name.setText(className);
    }

    @Override
    public void onPlaying(String type) {
        text_type.setText(type);
    }

    @Override
    public void onPlayingVideoProgress(Progress p) {
        text_class_progress.setText(p.getCurrentTime() + "/" + p.getTotalTime());
    }

    @Override
    public void onPlayingPPTProgress(String pageAt) {
        text_class_progress.setText("正在放映:" + pageAt + "页");
    }

    @Override
    public void onExceptiom() {
        enableInteractive(false);
    }

    @Override
    public void onCourseStart() {
        /**
         * 将课程信息上传至云端。
         */
        uploadClass(classes);
    }

    public void onCourseCompleted() {
        enableInteractive(false);
    }

    @Override
    public void onClassCompleted(String className) {
        List<Class> c = new ArrayList<Class>();
        Class cc = new Class();
        cc.setCompleted(true);
        cc.setTitle(className);
        c.add(cc);
        uploadClass(c);
    }

    @Override
    public void onSchoolURLNOTFound(String causeBy) {
        super.onSchoolURLNOTFound(causeBy);
        enableInteractive(false);
    }


    //------------------------------线程池和UI交互的接口 ↑↑↑-------------------------

    /**
     * 是否让用户交互
     * @param b true则交互
     */
    private void enableInteractive(boolean b){
        tf_username.setDisable(b);
        tf_password.setDisable(b);
        btn_login.setDisable(b);
        tf_course.setDisable(b);
        tf_unit.setDisable(b);
    }

    void uploadClass(List<Class> c_){
        Classes c = new Classes();
        c.setCourseTitle(tf_course.getText());
        c.setUid(uid);
        c.setUpdateTime(System.currentTimeMillis());
        c.setUsername(tf_username.getText());
        c.setClasses(c_);
        c.setUnit(tf_unit.getText());
        c.setRealName(name);
        UserUtil.create()
                .uploadClassesListener(c,this);
    }
}
