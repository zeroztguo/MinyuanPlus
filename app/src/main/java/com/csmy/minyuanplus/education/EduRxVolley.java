package com.csmy.minyuanplus.education;

import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventMsg;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.Course;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by Zero on 16/8/2.
 */
public class EduRxVolley {
    private static final String TAG = "EduOkHttp";
    public static final String CSMY_EDUCATION = "http://jw3.csmzxy.com:8088";
    public static final String CSMY_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CSMY_EDUCATION_HOME = "http://jw3.csmzxy.com:8088/default3.aspx";
    public static final String CSMY_BASE_URL_XH = "http://jw3.csmzxy.com:8088/xs_main.aspx?xh=";
    public static final String CSMY_BASE_URL_SCHEDULE_PART_ONE = "http://jw3.csmzxy.com:8088/xskbcx.aspx?xh=";
    public static final String CSMY_BASE_URL_SCHEDULE_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121603";
    public static final String CSMY_BASE_URL_PERSONAL_INFO_PART_ONE = "http://jw3.csmzxy.com:8088/xsgrxx.aspx?xh=";
    public static final String CSMY_BASE_URL_PERSONAL_INFO_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121501";
    public static final String CSMY_BASE_URL_GRADE_HOME_PART_ONE = "http://jw3.csmzxy.com:8088/xscjcx.aspx?xh=";
    public static final String CSMY_BASE_URL_GRADE_HOME_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121605";
    public static String CSMY_REFERER_URL = "http://jw3.csmzxy.com:8088/xs_main.aspx?xh=";
    public static final String COLLEGE_NEWS = "http://web.csmzxy.com/netCourse/readData";
    public static final String COOKIE = "Cookie";
    public static final String REFERER = "Referer";
    public static final String ORIGIN = "Origin";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String NOT_COMMON = "你还没有进行本学期的教学质量评价";


    public static void enterEducationHome() {
        new RxVolley.Builder()
                .url(CSMY_EDUCATION)
                .httpMethod(RxVolley.Method.GET)
                .encoding("gb2312")
                .callback(new HttpCallback() {
                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);

                        EduInfo.saveCookie(headers.get("Set-Cookie").toString());
                        try {
                            Logger.d("RxVolley进入教务系统主页成功" + new String(t, "gb2312"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        loignEducation();
                    }
                })
                .doTask();
    }

    public static void loignEducation() {
        Logger.d("RxVolley开始登陆。。。");

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.put("__VIEWSTATE", "dDw3OTkxMjIwNTU7Oz4BO%2FY3htAyQJF%2FNwKj5qZqDUYy9g%3D%3D");
        params.put("__VIEWSTATEGENERATOR", "92719903");
        params.put("TextBox1", EduInfo.getEducationUserName());
        params.put("TextBox2", EduInfo.getEducationPassword());
        params.put("TextBox3", "");
        params.put("RadioButtonList1", "%D1%A7%C9%FA");
        params.put("Button1", "");

        new RxVolley.Builder()
                .url(CSMY_EDUCATION_HOME)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding("gb2312")
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        try {
                            String body = new String(t, "gb2312");
                            Logger.d("登录成功:" + body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        enterEducationInterior();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        Logger.d("登录失败,code:" + errorNo);

                    }
                })
                .doTask();
    }

    public static void enterEducationInterior() {
        Logger.d("RxVolley开始进入教务系统内部。。。");

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("进入教务系统内部成功:" + body);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                switch (EventTag.getCurrentTag()) {
                    case EventTag.LOGIN_EDUCATION:
                        obtainPersonalInfo();
                        break;
                    case EventTag.SWITCH_SCHEDULE:
                        switchSchedule();
                        break;
                    case EventTag.QUERY_GRADE:
                        enterGradePage();
                        break;
                    case EventTag.QUERY_GRADE_FAIL:
                        enterGradePage();
                        break;
                    case EventTag.GRADE_STATISTICAL:
                        enterGradePage();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("进入教务系统内部失败,code is:" + errorNo);

                switch (EventTag.getCurrentTag()) {
                    case EventTag.LOGIN_EDUCATION:
                        Event.sendEmptyMessage(Event.EDUCATION_LOGIN_FAIL);
                        break;
                    case EventTag.SWITCH_SCHEDULE:
                        Event.sendEmptyMessage(Event.EDUCATION_SWITCH_SCHEDULE_FAIL);
                        break;
                    case EventTag.QUERY_GRADE:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL);
                        break;
                    case EventTag.QUERY_GRADE_FAIL:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL_FAIL);
                        break;
                    case EventTag.GRADE_STATISTICAL:
                        Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_FAIL);
                        break;

                    default:
                        break;
                }

            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_XH + EduInfo.getEducationUserName())
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();
    }


    public static void obtainSchedule() {

        Logger.d("RxVolley开始获取课表。。。");

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("获取课表成功:" + body);
                    //保存课表html
                    EduSchedule.saveScheduleHtml(body);
                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("获取课表失败,code is:" + errorNo);
            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO)
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();
    }

    public static void obtainPersonalInfo() {
        Logger.d("RxVolley开始获取个人信息。。。");

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("获取个人信息成功:" + body);
                    EduLogin.savePersonalInfoHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("获取个人信息失败,code is:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_PERSONAL_INFO_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_PERSONAL_INFO_PART_TWO)
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 选择课表
     */
    public static void switchSchedule() {
        HttpParams params = new HttpParams();
        params.put("__EVENTTARGET", "xqd");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", "dDwtODAxODI2NDQzO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw5Pjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDM%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8ND47QDwyMDE2LTIwMTc7MjAxNS0yMDE2OzIwMTQtMjAxNTtcZTs%2BO0A8MjAxNi0yMDE3OzIwMTUtMjAxNjsyMDE0LTIwMTU7XGU7Pj47bDxpPDE%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwxPjs%2BPjs7Pjt0PDtsPGk8MD47PjtsPHQ8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47O2w8aTwwPjs%2BPjs7Pjs%2BPjs%2BPjs%2BPjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BOz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bmo7bmo7Pj47Pjt0PGk8MTU%2BO0A8MjAxNjsyMDE1OzIwMTQ7MjAxMzsyMDEyOzIwMTE7MjAxMDsyMDA5OzIwMDg7MjAwNzsyMDA2OzIwMDU7MjAwNDsyMDAzO1xlOz47QDwyMDE2OzIwMTU7MjAxNDsyMDEzOzIwMTI7MjAxMTsyMDEwOzIwMDk7MjAwODsyMDA3OzIwMDY7MjAwNTsyMDA0OzIwMDM7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4eW1jO3h5ZG07Pj47Pjt0PGk8MTE%2BO0A85ZWG5a2m6ZmiO%2BeUteWtkOS%2FoeaBr%2BW3peeoi%2BWtpumZojvnpL7kvJrnrqHnkIblrabpmaI756S%2B5Lya5bel5L2c5a2m6ZmiO%2BauoeS7quWtpumZojvoibrmnK%2FlrabpmaI75paH5YyW5Lyg5pKt5a2m6ZmiO%2Bi9r%2BS7tuWtpumZojvljLvlrabpmaI75aSW6K%2Bt5a2m6ZmiO1xlOz47QDwxODsxOTsyMDsyMTsyMjsyMzsyNDsyNTsyNjsyNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHp5bWM7enlkbTs%2BPjs%2BO3Q8aTw4PjtAPOeUteWtkOWVhuWKoTvlm73pmYXnu4%2FmtY7kuI7otLjmmJM75Lya6K6hO%2BW4guWcuuiQpemUgDvnianmtYHnrqHnkIY76K%2BB5Yi45oqV6LWE5LiO566h55CGO%2BivgeWIuOS4juacn%2Bi0pztcZTs%2BO0A8MTgwNTsxODAzOzE4MDE7MTgwMjsxODA2OzE4MDQ7MTgwNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGJqbWM7YmpkbTs%2BPjs%2BO3Q8aTw0PjtAPOeUteWVhjE2MzM755S15ZWGMTYzMTvnlLXllYYxNjMyO1xlOz47QDwxNjE4MDUzMzsxNjE4MDUzMTsxNjE4MDUzMjtcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhtO3hoOz4%2BOz47dDxpPDE%2BO0A8XGU7PjtAPFxlOz4%2BOz47Oz47Pj47Pj47dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwyPjtpPDQ%2BO2k8Nj47aTw4PjtpPDExPjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNDI1MTEzMjEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpg63ms73mtps7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia77ya6L2v5Lu25oqA5pyv77yI56e75Yqo5bqU55So5byA5Y%2BR5pa55ZCR77yJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrnp7vliqgxNDMyOz4%2BOz47Oz47dDx0PDs7bDxpPDA%2BOz4%2BOzs%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwxPjtpPDE%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47bDxpPDA%2BOz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia5rex5bqm56S%2B5Lya5a6e6Le1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzliJjljZPlpoI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEuMDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MDItMTk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MT47aTwxPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDE%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwyMDE1LTIwMTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4k%2BS4mua3seW6puekvuS8muWunui3tTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85YiY5Y2T5aaCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxLjA7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjs%2BPjs%2B8U%2FrZesVxymifx8EKT7RuFOUgVc%3D");
        params.put("__VIEWSTATEGENERATOR", "55530A43");
        params.put("xnd", EduInfo.getCurrentAcademicYear());
        params.put("xqd", EduInfo.getCurrentTerm());
        params.putHeaders(COOKIE, EduInfo.getCookie());

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    if (body.contains(NOT_COMMON)) {
                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_SWITCH_SCHEDULE_FAIL, EventMsg.NEED_COMMON));
                        Logger.d("选择课表失败：" + NOT_COMMON);
                    } else {
                        Logger.d("选择课表成功");
                        EduSchedule.saveScheduleHtml(body);
                        List<Course> courseList = EduSchedule.getEducationScheduleCourses(false);
                        Logger.d("有多少门课：" + courseList.size());
                        for (Course course : courseList) {
                            Logger.d(course.toString());
                        }
                        DataSupport.saveAll(courseList);
                        Event.sendEmptyMessage(Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("选择课表失败,code is:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_SWITCH_SCHEDULE_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 进入成绩查询页面
     */
    public static void enterGradePage() {
        Logger.d("RxVolley开始进入查询成绩页面。。。");

        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("进入成绩查询页面成功,body length:" + body.length());
                    EduGrade.saveGradeHomeHtml(body);
                    EduGrade.saveViewState();

                    switch (EventTag.getCurrentTag()) {
                        case EventTag.QUERY_GRADE:
                            queryGrade();
                            break;
                        case EventTag.QUERY_GRADE_FAIL:
                            Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL_SUCCESS);
                            break;
                        case EventTag.GRADE_STATISTICAL:
                            gradeStatistical();
                            break;

                        default:
                            break;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("成绩查询失败,code is:" + errorNo);
                switch (EventTag.getCurrentTag()) {
                    case EventTag.QUERY_GRADE:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL);
                        break;
                    case EventTag.QUERY_GRADE_FAIL:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL_FAIL);
                        break;
                    case EventTag.GRADE_STATISTICAL:
                        Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_FAIL);
                        break;

                    default:
                        break;
                }
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 查询成绩
     */
    public static void queryGrade() {
        Logger.d("RxVolley开始查询成绩。。。");
        String view_state = EduGrade.getViewState();
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", view_state);
        params.put("__VIEWSTATEGENERATOR", "9727EB43");
        params.put("ddlXN", EduGrade.getGradeAcadamicYear());
        params.put("ddlXQ", EduGrade.getGradeTerm());
        params.put("ddl_kcxz", "");
        params.put("btn_xq", "%D1%A7%C6%DA%B3%C9%BC%A8");
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, url);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("成绩查询成功,body length:" + body.length());
                    EduGrade.saveGradeHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("成绩查询失败,code is:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();

    }


    /**
     * 成绩统计
     */
    public static void gradeStatistical() {
        Logger.d("RxVolley开始成绩统计。。。");
        String view_state = EduGrade.getViewState();
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", view_state);
        params.put("__VIEWSTATEGENERATOR", "9727EB43");
        params.put("ddlXN", "");
        params.put("ddlXQ", "");
        params.put("ddl_kcxz", "");
        params.put("Button1", "%B3%C9%BC%A8%CD%B3%BC%C6");
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, url);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, "gb2312");
                    Logger.d("成绩统计查询成功,body length:" + body.length());
                    EduGrade.saveGradeStatisticalHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("成绩统计查询失败,code is:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding("gb2312")
                .callback(httpCallback)
                .doTask();

    }
}
