package com.acong.chaoxingcrawl.utils.net;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ImageUtils {
    /**
     * 下载学习通登陆最新的验证码
     * @param url
     * @param cookies
     * @return
     */
    public static byte[] downloadCheckCode(String url,String cookies) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://passport2.chaoxing.com/num/code?" + System.currentTimeMillis())
                .addHeader("Cookie", cookies)
                .get()
                .build();

        //同步
        Call newCall = client.newCall(request);
        Response response = newCall.execute();

        if (response.code() == 200){
            return response.body().bytes();
        }else{
            return null;
        }
    }
}
