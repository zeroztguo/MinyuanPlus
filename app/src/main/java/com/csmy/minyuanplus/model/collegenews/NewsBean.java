package com.csmy.minyuanplus.model.collegenews;

import java.io.Serializable;

/**
 * 民院新闻的实体
 * Created by Zero on 16/8/15.
 */
public class NewsBean implements Serializable{
    //新闻ID
    private int contentID;
    //新闻作者
    private String contentAuthor;
    //新闻标题
    private String contentTitle;
    //发表时间
    private String submitTime;
    //地址
    private String shareUrl;

    public int getContentID() {
        return contentID;
    }

    public NewsBean setContentID(int contentID) {
        this.contentID = contentID;
        return this;
    }

    public String getContentAuthor() {
        return contentAuthor;
    }

    public NewsBean setContentAuthor(String contentAuthor) {
        this.contentAuthor = contentAuthor;
        return this;

    }

    public String getContentTitle() {
        return contentTitle;
    }

    public NewsBean setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
        return this;

    }

    public String getSubmitTime() {
        return submitTime;
    }

    public NewsBean setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
        return this;

    }

    public String getShareUrl() {
        return shareUrl;

    }

    public NewsBean setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
        return this;

    }
}
