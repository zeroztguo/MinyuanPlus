package com.csmy.minyuanplus.model.afterclass;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 微信精选实体类
 * Created by Zero on 16/9/3.
 */
public class Weixin extends DataSupport implements Serializable{
    String ctime;
    String title;
    String description;
    String picUrl;
    String url;

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
