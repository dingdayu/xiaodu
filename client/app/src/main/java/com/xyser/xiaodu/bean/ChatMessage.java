package com.xyser.xiaodu.bean;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by dingdayu on 2016/11/7.
 */

public class ChatMessage {
    private String name;
    private String msg;
    private Type type;
    private Date date;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public enum  Type
    {
        INCOMING, OUTCOMING, INIMAGE, OUTIMAGE
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ChatMessage() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ChatMessage(String msg, Type type, Date date) {
        super();
        this.msg = msg;
        this.type = type;
        this.date = date;
    }
}
