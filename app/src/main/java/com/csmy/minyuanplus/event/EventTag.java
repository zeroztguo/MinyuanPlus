package com.csmy.minyuanplus.event;

import com.csmy.minyuanplus.support.util.SPUtil;

/**
 * EventBus事件标识
 * Created by Zero on 16/6/29.
 */
public class EventTag {
    /**
     * 登录教务系统
     */
    public static final int LOGIN_EDUCATION = 0;
    /**
     * 选择课表
     */
    public static final int SWITCH_SCHEDULE = 1;
    /**
     * 查询成绩
     */
    public static final int QUERY_GRADE =2;
    /**
     * 成绩统计
     */
    public static final int GRADE_STATISTICAL =3;
    /**
     * 查询未通过成绩
     */
    public static final int QUERY_GRADE_FAIL = 4;
    /**
     * 查询绩点
     */
    public static final int QUERY_GRADE_GPA = 5;


    private static final String CURRENT_TAG = "current_tag";

    public static int getCurrentTag() {
        return (int) SPUtil.get(CURRENT_TAG,LOGIN_EDUCATION);
    }

    public static void saveCurrentTag(int currentTag) {
        SPUtil.put(CURRENT_TAG,currentTag);
    }
}
