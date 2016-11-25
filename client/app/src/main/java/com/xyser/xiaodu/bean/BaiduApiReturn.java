package com.xyser.xiaodu.bean;

/**
 * Created by dingdayu on 2016/11/25.
 */

/**
 * 百度api返回
 */
public class BaiduApiReturn {
    private int Type;
    private String data;

    public BaiduApiReturn(int type, String data) {
        Type = type;
        this.data = data;
    }

    public int getType() {
        return Type;
    }

    public BaiduApiReturn() {
    }

    public void setType(int type) {
        Type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
