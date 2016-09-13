package com.csmy.minyuanplus.support;

import com.csmy.minyuanplus.support.util.SPUtil;

/**
 * 通知的帮助类
 * Created by Zero on 16/8/26.
 */
public class Notification {
    //未读通知数
    private static final String UNREAD_NOTIFY = "unread_notify";
    //最新的消息编号
    private static final String LATEST_NOTIFY_CODE = "latest_notify_code";

    public  static void setUnreadNotify(int i){
        SPUtil.put(UNREAD_NOTIFY,i);
    }

    /**
     * @return 未读通知数
     */
    public static int getUnreadNotify(){
        return (int) SPUtil.get(UNREAD_NOTIFY,0);
    }

    public static void setLatestNotifyCode(int code){
        SPUtil.put(LATEST_NOTIFY_CODE,code);
    }

    /**
     * @return 最新消息编号
     */
    public static int getLatestNotifyCode(){
        return (int) SPUtil.get(LATEST_NOTIFY_CODE,0);
    }


}
