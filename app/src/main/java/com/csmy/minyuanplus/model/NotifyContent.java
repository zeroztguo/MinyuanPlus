package com.csmy.minyuanplus.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 通知内容实体类
 * Created by Zero on 16/8/26.
 */
public class NotifyContent extends DataSupport implements Serializable{
    String title;
    String date;
    String url;
    String type;
    String notifyCode;
    /**
     * 是否已读
     */
    boolean isRead;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotifyCode() {
        return notifyCode;
    }

    public void setNotifyCode(String notifyCode) {
        this.notifyCode = notifyCode;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "NotifyContent{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", notifyCode='" + notifyCode + '\'' +
                '}';
    }


}
