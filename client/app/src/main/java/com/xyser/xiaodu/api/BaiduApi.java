package com.xyser.xiaodu.api;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xyser.xiaodu.bean.BaiduApiReturn;
import com.xyser.xiaodu.bean.ChatMessage;
import com.xyser.xiaodu.unit.OkHttpClientManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by dingdayu on 2016/11/18.
 */

public class BaiduApi {

    private static String TAG = "BaiduApi";

    private static final String ak = "B90ac8de3bae977169608b3208eaf39f";
    private static final String sk = "584d3e8c37ce3a2ff6c7f12f01ca2047";

    private static final String Transfer = "http://xiaodu.dingxiaoyu.cn/ocr.php";

    /**
     * 直接通过图片返回文字信息
     * @param bitmap
     * @return
     */
    public static String ocrChat(Bitmap bitmap)
    {
        OkHttpClientManager http = OkHttpClientManager.getInstance();
        String ret = "图片识别失败";

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            ret = http.postFile(Transfer + "?type=chat", data);

            // 解析json
            JSONObject jsonObject = JSON.parseObject(ret);

            int code = jsonObject.getIntValue("code");
            Log.e(TAG, "ocrChat:" + code);
            if(code == 200) {
                ret = jsonObject.getString("data");
            } else {
                ret = "图片识别失败";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "ocrChat:" + ret);
        return ret;
    }

    /**
     * 通过固态盘返回
     * @param bitmap
     * @return
     */
    public static BaiduApiReturn getOcr(Bitmap bitmap)
    {
        BaiduApiReturn retBaiduApi = new BaiduApiReturn(300, "图片识别识别");

        OkHttpClientManager http = OkHttpClientManager.getInstance();

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            String ret = http.postFile(Transfer + "?type=chat", data);
            Log.i("ocrChat:", ret);
            // 解析json
            JSONObject jsonObject = JSON.parseObject(ret);

            int code = jsonObject.getIntValue("code");
            Log.e(TAG, "ocrChat:" + code);
            if(code == 200) {
                ret = jsonObject.getString("data");
                retBaiduApi.setType(200);
                retBaiduApi.setData(ret);
            } else if (code == 201) {
                ret = jsonObject.getString("data");
                retBaiduApi.setType(201);
                retBaiduApi.setData(ret);
            } else {
                retBaiduApi.setType(300);
                retBaiduApi.setData("图片识别识别");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return retBaiduApi;
    }

    /**
     * 发送ocr识别请求
     * @param str
     * @return
     */
    public static String ocr(String str)
    {
        OkHttpClientManager http = OkHttpClientManager.getInstance();

        String json = creatParam(str);
        try {
            String ret = http.post(Transfer, json);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 拼装请求参数
     * @param var
     * @return
     */
    private static String creatParam(String var)
    {
        String ret = "{\"image\": \" "+ var+"\"}";
        return ret;
    }

}