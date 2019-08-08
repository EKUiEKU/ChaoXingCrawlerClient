package com.acong.chaoxingcrawl.utils;

import com.google.gson.Gson;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DamagouUtil {
    private static DamagouUtil mUtils;

    private OkHttpClient client;

    private String url_login = "http://www.damagou.top/apiv1/login.html";
    private String url_dama  = "http://www.damagou.top/apiv1/recognize.html";

    private String appKey = "3905f71c59209b3db913f62493a7ff34";
    private DamagouUtil(){
        client = new OkHttpClient();
    }

    public static DamagouUtil create(){
        synchronized (DamagouUtil.class){
            if (mUtils == null){
                synchronized (DamagouUtil.class){
                    mUtils = new DamagouUtil();
                }
            }
        }

        return mUtils;
    }


    public interface OnLoginListener{
        void OnLoginSuccess(String appKey);
        void OnLoginFailure(Exception e);
    }
    /**
     *
     * @param user    打码狗账号
     * @param pwd     打码狗密码
     * @return        App_key
     */
    public void login(String user, String pwd, final OnLoginListener listener){
        if (listener == null)
            return;

        HttpUrl url = HttpUrl.parse(url_login)
                .newBuilder()
                .addQueryParameter("username", user)
                .addQueryParameter("password", pwd)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.OnLoginFailure(e);
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();

                if (data.length() >= 15){
                    listener.OnLoginSuccess(data);
                    appKey = data;
                }else{
                    listener.OnLoginFailure(new Exception(data));
                }
            }
        });
    }

    /**
     * On打码Listener
     */
    public interface OnDamaListener{
        void OnDamaSuccess(String code);
        void OnDamaFailure(Exception e);
    }

    public void dama(byte[] bimg,String type, final OnDamaListener listener){
        if (appKey == null || listener == null)
            return;

        String img = new BASE64Encoder().encode(bimg);


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map<String,String > form = new HashMap<String, String>();
        form.put("userkey",appKey);
        form.put("type","1002");
        form.put("isJson","1");
        form.put("image",img);

        RequestBody body = RequestBody.create(JSON, new Gson().toJson(form));

        //System.out.println(img);

        final Request request = new Request.Builder()
                .url(url_dama)
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.OnDamaFailure(e);
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String code = response.body().string();

                listener.OnDamaSuccess(code);
                response.close();
            }
        });


    }

    private String loadImageToBase64(String url){
        Request request = new Request.Builder().url(url).build();
        Response execute =null;
        try {
            execute = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = null;
        try {
            data = execute.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BASE64Encoder().encode(data);
    }


    public class TYPE{
        public static final String TYPE_ONLY_NUMBSERS = "1002";
        public static final String TYPE_NUMBERS_ALPHABET = "1001";
    }
}
