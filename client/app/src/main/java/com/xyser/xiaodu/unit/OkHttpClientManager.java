package com.xyser.xiaodu.unit;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by dingdayu on 2016/11/17.
 */

public class OkHttpClientManager {

    private static OkHttpClientManager mInstance;
    protected OkHttpClient mOkHttpClient = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/octet-stream");

    public static OkHttpClientManager getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpClientManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public String get(String url) throws IOException {
        //创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //同步请求
        Response execute = call.execute();
        return execute.body().string();
    }

    public String post(String url, String json) throws IOException
    {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

    public String postFile(String url, byte[] file) throws IOException {
        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        Log.i("OkHttpClientManager", "code : " + response.code());
        return response.body().string();
    }
}
