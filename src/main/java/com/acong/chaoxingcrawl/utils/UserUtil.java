package com.acong.chaoxingcrawl.utils;

import com.acong.chaoxingcrawl.bean.Classes;
import com.acong.chaoxingcrawl.bean._LoginBean;
import com.acong.chaoxingcrawl.bean._UserInfo;
import com.acong.chaoxingcrawl.utils.interfaces.OnUploadClassesListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import interfaces.OnLoginListener;
import interfaces.OnUploadInfoListener;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户操作工具类
 */
public class UserUtil {
    public static final String BASE_URL = "http://47.102.130.244/";
    public static final String LOGIN_URL = BASE_URL + "api/login";
    public static final String REGISTER_URL = BASE_URL + "api/register";
    public static final String UPLOAD_INFO = BASE_URL + "user/upload";
    public static final String UPLOAD_CLASSES = BASE_URL + "user/uploadClasses";

    private OkHttpClient client;
    private static UserUtil util;

    private HashMap<HttpUrl,List<Cookie>> cookieStore=new HashMap<HttpUrl, List<Cookie>>();

    private UserUtil(){
        client = new OkHttpClient.Builder().connectTimeout(1000*10, TimeUnit.SECONDS).cookieJar(new CookieJar() {
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                cookieStore.put(httpUrl, list);
                cookieStore.put(HttpUrl.parse(LOGIN_URL), list);
            }

            @NotNull
            public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(HttpUrl.parse(LOGIN_URL));
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
    }

    public static UserUtil create(){
        synchronized (UserUtil.class){
            if (util == null){
                synchronized (UserUtil.class){
                    util = new UserUtil();
                }
            }
        }
        return util;
    }

    private Long uid;


    /**
     * 用户登陆
     * @param username
     * @param password
     */
    public void login(String username,String password,@NotNull final OnLoginListener listener){
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("rememberMe","1")
                .build();

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(formBody)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onLoginFailure("网络请求失败");
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String body = response.body().string();
                Gson gson = new Gson();
                _LoginBean loginBean = gson.fromJson(body, _LoginBean.class);

                if (loginBean.getResult().equals("登陆成功")){
                    uid = loginBean.getUid();
                    listener.onLoginSuccess(loginBean.getUid());
                }else{
                    listener.onLoginFailure(loginBean.getResult());
                }
            }
        });
    }


    public void uploadInfo(@NotNull _UserInfo info,@NotNull final OnUploadInfoListener listener){
        final String json = new Gson().toJson(info);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        final Request request = new Request.Builder()
                .url(UPLOAD_INFO)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onUploadFailure("网络错误");
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String result = response.body().string();
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)parser.parse(result);
                    String result1 = jsonObject.get("result").getAsString();
                    if (result1.equals("success")){
                        listener.onUploadSuccess();
                    }else{
                        listener.onUploadFailure("上传数据不正确");
                    }
                }catch (Exception e){
                    listener.onUploadFailure("没有登陆。");
                }
            }
        });
    }


    /**
     * 上传课程数据
     */
    private OnUploadClassesListener uploadClassesListener;

    public void uploadClassesListener(Classes classes, final OnUploadClassesListener listener){
        String json = new Gson().toJson(classes, Classes.class);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        final Request request = new Request.Builder()
                .url(UPLOAD_CLASSES)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.uploadFailure("网络错误");
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String result = response.body().string();
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = (JsonObject)parser.parse(result);
                    String result1 = jsonObject.get("result").getAsString();
                    if (result1.equals("success")){
                        listener.uploadSuccess();
                    }else{
                        listener.uploadFailure("上传数据不正确");
                    }
                }catch (Exception e){
                    listener.uploadFailure("没有登陆。");
                }
            }
        });
    }
}
