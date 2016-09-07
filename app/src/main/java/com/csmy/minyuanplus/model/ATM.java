package com.csmy.minyuanplus.model;

import org.litepal.crud.DataSupport;

/**
 * 民院AMT机信息实体类
 * Created by Zero on 16/8/29.
 */
public class ATM extends DataSupport {
    int id;
    //ATM 简信
    String info;
    //图片地址
    String imgUrl;
    //图片二进制
    byte[] img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
