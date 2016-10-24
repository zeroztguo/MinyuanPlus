package com.csmy.minyuanplus.event;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBus 事件编号
 * Created by Zero on 16/5/28.
 */
public class Event {

    //登录教务系统成功
    public static final int EDUCATION_LOGIN_SUCCESS = 2;
    //登录教务系统失败
    public static final int EDUCATION_LOGIN_FAIL = 3;
    //获取课表成功
    public static final int EDUCATION_OBTAIN_SCHEDULE_SUCCESS = 6;
    //获取课表失败
    public static final int EDUCATION_OBTAIN_SCHEDULE_FAIL = 7;
    //获取课表
    public static final int EDUCATION_OBTAIN_SCHEDULE = 8;
    //获取个人信息成功
    public static final int EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS = 9;
    //获取个人信息失败
    public static final int EDUCATION_OBTAIN_PERSONAL_INFO_FAIL = 10;
    //查询成绩成功
    public static final int EDUCATION_QUERY_GRADE_SUCCESS = 11;
    //查询成绩失败
    public static final int EDUCATION_QUERY_GRADE_FAIL = 12;
    //切换课表成功
    public static final int EDUCATION_SWITCH_SCHEDULE_SUCCESS = 13;
    //切换课表失败
    public static final int EDUCATION_SWITCH_SCHEDULE_FAIL = 14;
    //查询成绩统计成功
    public static final int EDUCATION_GRADE_STATISTICAL_SUCCESS = 15;
    //查询成绩统计失败
    public static final int EDUCATION_GRADE_STATISTICAL_FAIL = 16;
    //查询绩点成功
    public static final int EDUCATION_QUERY_GRADE_GPA_SUCCESS = 17;
    //查询绩点失败
    public static final int EDUCATION_QUERY_GRADE_GPA_FAIL = 18;
    //获取学院新闻
    public static final int NEWS_COLLEGE = 19;

    //查询未通过成绩成功
    public static final int EDUCATION_QUERY_GRADE_FAIL_SUCCESS = 20;
    //查询未通过成绩失败
    public static final int EDUCATION_QUERY_GRADE_FAIL_FAIL = 21;
    //切换到登录界面
    public static final int SWITCH_LOGIN_PAGE = 22;
    //新消息
    public static final int NOTIFY_UPDATE = 23;
    //更新应用
    public static final int UPDATE_APPLICATION = 24;
    //修改用户头像
    public static final int UPDATE_USER_ICON = 25;
    //选择课表成功但没有课
    public static final int SWITCH_SCHEDULE_NO_COURSE = 26;

    public static void sendEmptyMessage(int event) {
        EventBus.getDefault().post(new EventModel<String>(event));
    }

    public static void sendIntMessage(int event, int message) {
        EventBus.getDefault().post(new EventModel<Integer>(event, message));
    }

}
