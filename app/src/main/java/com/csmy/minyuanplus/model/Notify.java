package com.csmy.minyuanplus.model;

import java.util.ArrayList;

/**
 * 解析通知列表的实体
 * Created by Zero on 16/8/26.
 */
public class Notify {
    //最后一条通知编号
    String latestNotifyCode;
    ArrayList<NotifyContent> notifyList = new ArrayList<>();

    public String getLatestNotifyCode() {
        return latestNotifyCode;
    }

    public void setLatestNotifyCode(String latestNotifyCode) {
        this.latestNotifyCode = latestNotifyCode;
    }

    public ArrayList<NotifyContent> getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(ArrayList<NotifyContent> notifyList) {
        this.notifyList = notifyList;
    }
}
