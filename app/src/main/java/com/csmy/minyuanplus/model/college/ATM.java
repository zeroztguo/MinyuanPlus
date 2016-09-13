package com.csmy.minyuanplus.model.college;

import org.litepal.crud.DataSupport;

/**
 * 民院AMT机信息实体类
 * Created by Zero on 16/8/29.
 */
public class ATM extends DataSupport {
    int id;
    //ATM 简信
    String info;
    //位置信息
    String location;
    //图片地址
    String imgUrl;
    //图片二进制
    byte[] img;

    boolean isHaveImg;

    public boolean isHaveImg() {
        return isHaveImg;
    }

    public void setHaveImg(boolean haveImg) {
        isHaveImg = haveImg;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
