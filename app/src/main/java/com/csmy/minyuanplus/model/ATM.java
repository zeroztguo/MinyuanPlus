package com.csmy.minyuanplus.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 16/8/29.
 */
public class ATM extends DataSupport{
    String info;
    String img;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
