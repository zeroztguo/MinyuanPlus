package com.csmy.minyuanplus.model;

/**
 * 设置实体
 * Created by Zero on 16/8/17.
 */
public class Setting {
    String title;
    String subTitle;
    boolean  isSwitch;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isSwitch() {
        return isSwitch;
    }

    public void setSwitch(boolean aSwitch) {
        isSwitch = aSwitch;
    }
}
