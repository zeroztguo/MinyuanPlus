package com.csmy.minyuanplus.model.afterclass;

import java.util.ArrayList;

/**
 * 微信精选gson解析实体类
 * Created by Zero on 16/9/3.
 */
public class WeixinInfo {
    private int code;
    private String message;
    private ArrayList<Weixin> newslist = new ArrayList<>();

    public ArrayList<Weixin> getNewslist() {
        return newslist;
    }

    public void setNewslist(ArrayList<Weixin> newslist) {
        this.newslist = newslist;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
