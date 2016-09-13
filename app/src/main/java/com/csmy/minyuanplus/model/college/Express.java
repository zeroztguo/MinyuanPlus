package com.csmy.minyuanplus.model.college;

import org.litepal.crud.DataSupport;

/**
 * 快递实体类
 * Created by Zero on 16/9/8.
 */
public class Express extends DataSupport {
    int id;
    String name;
    String time;
    String location;
    String telephone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
