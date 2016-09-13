package com.csmy.minyuanplus.model.afterclass;

import org.litepal.crud.DataSupport;

/**
 * 果壳 新闻头信息实体
 * Created by Zero on 16/8/14.
 */
public class GuokrHeader extends DataSupport{
    //标题
    String title;
    //小图
    String small_image;
    //梗概
    String summary;
    //地址
    String resource_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmall_image() {
        return small_image;
    }

    public void setSmall_image(String small_image) {
        this.small_image = small_image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {

        this.resource_url = resource_url;
    }

    @Override
    public String toString() {
        return "GuokeHeader{" +
                "title='" + title + '\'' +
                ", small_image='" + small_image + '\'' +
                ", summary='" + summary + '\'' +
                ", resource_url='" + resource_url + '\'' +
                '}';
    }
}
