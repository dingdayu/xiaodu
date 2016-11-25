package com.xyser.xiaodu.api;

import com.alibaba.fastjson.JSON;
import com.xyser.xiaodu.bean.ChatMessage;
import com.xyser.xiaodu.unit.OkHttpClientManager;

import java.io.IOException;
import java.util.Date;

/**
 * Created by dingdayu on 2016/11/17.
 */

public class TulingRobot {

    private static final String URL = "http://www.tuling123.com/openapi/api";
    private static final String API_KEY = "256c015dfde964623b3e6be415607da9";

    /**
     * 发送一个消息，得到返回的消息
     * @param msg
     * @return
     */
    public static ChatMessage sendMessage(String msg) throws IOException {
        ChatMessage chatMessage = new ChatMessage();
        OkHttpClientManager http = OkHttpClientManager.getInstance();

        String json = creatParam(msg , "北京");
        String ret = http.post(URL, json);
        try
        {
            Result result = JSON.parseObject(ret, Result.class);
            chatMessage.setMsg(result.getText());
        } catch (Exception e)
        {
            chatMessage.setMsg("服务器繁忙，请稍候再试");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(ChatMessage.Type.INCOMING);
        return chatMessage;
    }

    private static String creatParam(String var, String loc)
    {
        PostParam postParam = new PostParam(var, loc, "342432342342");

        String json = JSON.toJSONString(postParam);
        return json;
    }

    private static class Result{
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        String code;
        String text;
    }

    private static class PostParam{
        String key = API_KEY;
        String info = API_KEY;
        String loc = API_KEY;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getLoc() {
            return loc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        String userid = "121212121";

        public PostParam() {
        }

        public PostParam(String info, String loc, String userid) {
            this.info = info;
            this.loc = loc;
            this.userid = userid;
        }
    }
}
