package com.csmy.minyuanplus.support.education;

import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.Course;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 教务系统需要的网络请求类
 * 请求失败需清除缓存
 * Created by Zero on 16/8/2.
 */
public class EduRxVolley {

    /**
     * 需要的常量
     */
    private static final String CSMY_EDUCATION = "http://jw3.csmzxy.com:8088";
    private static final String CSMY_EDUCATION_HOME = "http://jw3.csmzxy.com:8088/default3.aspx";
    public static final String CSMY_BASE_URL_XH = "http://jw3.csmzxy.com:8088/xs_main.aspx?xh=";
    private static final String CSMY_BASE_URL_SCHEDULE_PART_ONE = "http://jw3.csmzxy.com:8088/xskbcx.aspx?xh=";
    private static final String CSMY_BASE_URL_SCHEDULE_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121603";
    private static final String CSMY_BASE_URL_PERSONAL_INFO_PART_ONE = "http://jw3.csmzxy.com:8088/xsgrxx.aspx?xh=";
    private static final String CSMY_BASE_URL_PERSONAL_INFO_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121501";
    private static final String CSMY_BASE_URL_GRADE_HOME_PART_ONE = "http://jw3.csmzxy.com:8088/xscjcx.aspx?xh=";
    private static final String CSMY_BASE_URL_GRADE_HOME_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121605";
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String REFERER = "Referer";
    private static final String USER_AGENT = "User-Agent";
    //    private static final String USER_AGENT_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";
    private static final String USER_AGENT_CHROME = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)";
    private static final String NOT_COMMON = "你还没有进行本学期的教学质量评价";
    private static final String GB2312 = "gb2312";
    private static final String LOGIN_VIEWSTATE = "__VIEWSTATE";
    private static final String LOGIN_VIEWSTATE_VALUE = "dDwtMTM2MTgxNTk4OTs7Pof5kqHpeaUSQgt4AgvDD0O%2F1ELE";
    private static final String LOGIN_VIEWSTATEGENERATOR = "__VIEWSTATEGENERATOR";
    private static final String LOGIN_SCHEDULE_VIEWSTATEGENERATOR_VALUE = "94A5C162";
    private static final String LOGIN_TEXT_BOX_ONE = "TextBox1";
    private static final String LOGIN_TEXT_BOX_TWO = "TextBox2";
    private static final String LOGIN_TEXT_BOX_THREE = "TextBox3";
    private static final String LOGIN_RADIO_BUTTON_LIST = "RadioButtonList1";
    private static final String LOGIN_RADIO_BUTTON_LIST_VALUE = "%D1%A7%C9%FA";
    private static final String BUTTON_ONE = "Button1";
    private static final String BUTTON_ONE_VALUE = "+%B5%C7+%C2%BC+";
    private static final String DDL_JS = "ddl_js";
    private static final String DDL_JS_VALUE = "%D1%A7%C9%FA";
    private static final String EMPTY = "";
    private static final String SCHEDULE_XND = "xnd";
    private static final String SCHEDULE_XND_VIEWSTATE = "dDwtODAxODI2NDQzO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw5Pjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDM%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8ND47QDwyMDE2LTIwMTc7MjAxNS0yMDE2OzIwMTQtMjAxNTtcZTs%2BO0A8MjAxNi0yMDE3OzIwMTUtMjAxNjsyMDE0LTIwMTU7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwwPjs%2BPjs7Pjt0PDtsPGk8MD47PjtsPHQ8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47O2w8aTwwPjs%2BPjs7Pjs%2BPjs%2BPjs%2BPjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BOz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bmo7bmo7Pj47Pjt0PGk8MTU%2BO0A8MjAxNjsyMDE1OzIwMTQ7MjAxMzsyMDEyOzIwMTE7MjAxMDsyMDA5OzIwMDg7MjAwNzsyMDA2OzIwMDU7MjAwNDsyMDAzO1xlOz47QDwyMDE2OzIwMTU7MjAxNDsyMDEzOzIwMTI7MjAxMTsyMDEwOzIwMDk7MjAwODsyMDA3OzIwMDY7MjAwNTsyMDA0OzIwMDM7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4eW1jO3h5ZG07Pj47Pjt0PGk8MTE%2BO0A85ZWG5a2m6ZmiO%2BeUteWtkOS%2FoeaBr%2BW3peeoi%2BWtpumZojvnpL7kvJrnrqHnkIblrabpmaI756S%2B5Lya5bel5L2c5a2m6ZmiO%2BauoeS7quWtpumZojvoibrmnK%2FlrabpmaI75paH5YyW5Lyg5pKt5a2m6ZmiO%2Bi9r%2BS7tuWtpumZojvljLvlrabpmaI75aSW6K%2Bt5a2m6ZmiO1xlOz47QDwxODsxOTsyMDsyMTsyMjsyMzsyNDsyNTsyNjsyNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHp5bWM7enlkbTs%2BPjs%2BO3Q8aTw4PjtAPOeUteWtkOWVhuWKoTvlm73pmYXnu4%2FmtY7kuI7otLjmmJM75Lya6K6hO%2BW4guWcuuiQpemUgDvnianmtYHnrqHnkIY76K%2BB5Yi45oqV6LWE5LiO566h55CGO%2BivgeWIuOS4juacn%2Bi0pztcZTs%2BO0A8MTgwNTsxODAzOzE4MDE7MTgwMjsxODA2OzE4MDQ7MTgwNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGJqbWM7YmpkbTs%2BPjs%2BO3Q8aTw0PjtAPOeUteWVhjE2MzM755S15ZWGMTYzMTvnlLXllYYxNjMyO1xlOz47QDwxNjE4MDUzMzsxNjE4MDUzMTsxNjE4MDUzMjtcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhtO3hoOz4%2BOz47dDxpPDE%2BO0A8XGU7PjtAPFxlOz4%2BOz47Oz47Pj47Pj47dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwyPjtpPDQ%2BO2k8Nj47aTw4PjtpPDExPjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNDI1MTEzMjEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpg63ms73mtps7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia77ya6L2v5Lu25oqA5pyv77yI56e75Yqo5bqU55So5byA5Y%2BR5pa55ZCR77yJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrnp7vliqgxNDMyOz4%2BOz47Oz47dDx0PDs7bDxpPDA%2BOz4%2BOzs%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs7Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2B83IPPlb%2F8K0UxntU2O8KA%2Bcr2PE%3D";
    private static final String SCHEDULE_XQD = "xqd";
    private static final String SCHEDULE_XQD_VIEWSTATE = "dDwtODAxODI2NDQzO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw5Pjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDM%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8ND47QDwyMDE2LTIwMTc7MjAxNS0yMDE2OzIwMTQtMjAxNTtcZTs%2BO0A8MjAxNi0yMDE3OzIwMTUtMjAxNjsyMDE0LTIwMTU7XGU7Pj47bDxpPDE%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwwPjs%2BPjs7Pjt0PDtsPGk8MD47PjtsPHQ8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47O2w8aTwwPjs%2BPjs7Pjs%2BPjs%2BPjs%2BPjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BOz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bmo7bmo7Pj47Pjt0PGk8MTU%2BO0A8MjAxNjsyMDE1OzIwMTQ7MjAxMzsyMDEyOzIwMTE7MjAxMDsyMDA5OzIwMDg7MjAwNzsyMDA2OzIwMDU7MjAwNDsyMDAzO1xlOz47QDwyMDE2OzIwMTU7MjAxNDsyMDEzOzIwMTI7MjAxMTsyMDEwOzIwMDk7MjAwODsyMDA3OzIwMDY7MjAwNTsyMDA0OzIwMDM7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4eW1jO3h5ZG07Pj47Pjt0PGk8MTE%2BO0A85ZWG5a2m6ZmiO%2BeUteWtkOS%2FoeaBr%2BW3peeoi%2BWtpumZojvnpL7kvJrnrqHnkIblrabpmaI756S%2B5Lya5bel5L2c5a2m6ZmiO%2BauoeS7quWtpumZojvoibrmnK%2FlrabpmaI75paH5YyW5Lyg5pKt5a2m6ZmiO%2Bi9r%2BS7tuWtpumZojvljLvlrabpmaI75aSW6K%2Bt5a2m6ZmiO1xlOz47QDwxODsxOTsyMDsyMTsyMjsyMzsyNDsyNTsyNjsyNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHp5bWM7enlkbTs%2BPjs%2BO3Q8aTw4PjtAPOeUteWtkOWVhuWKoTvlm73pmYXnu4%2FmtY7kuI7otLjmmJM75Lya6K6hO%2BW4guWcuuiQpemUgDvnianmtYHnrqHnkIY76K%2BB5Yi45oqV6LWE5LiO566h55CGO%2BivgeWIuOS4juacn%2Bi0pztcZTs%2BO0A8MTgwNTsxODAzOzE4MDE7MTgwMjsxODA2OzE4MDQ7MTgwNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGJqbWM7YmpkbTs%2BPjs%2BO3Q8aTw0PjtAPOeUteWVhjE2MzM755S15ZWGMTYzMTvnlLXllYYxNjMyO1xlOz47QDwxNjE4MDUzMzsxNjE4MDUzMTsxNjE4MDUzMjtcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhtO3hoOz4%2BOz47dDxpPDE%2BO0A8XGU7PjtAPFxlOz4%2BOz47Oz47Pj47Pj47dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwyPjtpPDQ%2BO2k8Nj47aTw4PjtpPDExPjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNDI1MTEzMjEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpg63ms73mtps7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia77ya6L2v5Lu25oqA5pyv77yI56e75Yqo5bqU55So5byA5Y%2BR5pa55ZCR77yJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrnp7vliqgxNDMyOz4%2BOz47Oz47dDx0PDs7bDxpPDA%2BOz4%2BOzs%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs7Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47Pj47Pj47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MD47aTwwPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BWD737S6RyElTSumOGIwUqYj2mzg%3D";
    private static final String EVENTTARGET = "__EVENTTARGET";
    private static final String EVENTARGUMENT = "__EVENTARGUMENT";
    private static final String VIEWSTATE = "__VIEWSTATE";
    private static final String VIEWSTATEGENERATOR = "__VIEWSTATEGENERATOR";
    private static final String SCHEDULE_VIEWSTATEGENERATOR_VALUE = "55530A43";
    private static final String GRADE_VIEWSTATEGENERATOR_VALUE = "9727EB43";
    private static final String DDL_XN = "ddlXN";
    private static final String DDL_XQ = "ddlXQ";
    private static final String DDL_KCXZ = "ddl_kcxz";
    private static final String BTN_XQ = "btn_xq";
    private static final String BTN_XQ_VALUE = "%D1%A7%C6%DA%B3%C9%BC%A8";
    private static final String GRADE_STATISTICAL_BUTTON_ONE_VALUE = "%B3%C9%BC%A8%CD%B3%BC%C6";
    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_VALUE = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";
    private static final String ACCEPT_ENCODING_INTERIOR_VALUE = "gzip, deflate, sdch";
    private static final String ACCEPT_LANGUAGE = "Accept-Language";
    private static final String ACCEPT_LANGUAGE_VALUE = "zh-CN,zh;q=0.8,en;q=0.6";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_CONTROL_VALUE = "max-age=0";
    private static final String CONNECTION = "Connection";
    private static final String CONNECTION_VALUE = "keep-alive";

    private static final String SWITCH_SCHEDULE_DELETE_WHERE = "academicYear = ? and term = ?";


    public static void enterEducationHome() {
        HttpParams params = new HttpParams();
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);
        params.putHeaders(CACHE_CONTROL, CACHE_CONTROL_VALUE);
        params.putHeaders(ACCEPT, ACCEPT_VALUE);
        params.putHeaders(ACCEPT_ENCODING, ACCEPT_ENCODING_INTERIOR_VALUE);
        params.putHeaders(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);

        new RxVolley.Builder()
                .url(CSMY_EDUCATION_HOME)
                .httpMethod(RxVolley.Method.GET)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(new HttpCallback() {
                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Logger.d("进入教务系统主页失败");


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
                            case EventTag.QUERY_GRADE_GPA:
                                Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_FAIL);
                                break;

                            default:
                                break;
                        }
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);

                        EduInfo.saveCookie(headers.get(SET_COOKIE).toString());
                        Logger.d("RxVolley进入教务系统主页成功,Cookie:" + EduInfo.getCookie());

                        loginEducation();
                    }
                })
                .doTask();
    }

    /**
     * 登录教务系统
     */
    public static void loginEducation() {
        Logger.d("开始登录教务系统。。。");

        HttpParams params = new HttpParams();

        params.putHeaders(ACCEPT, ACCEPT_VALUE);
        params.putHeaders(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
        params.putHeaders(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);
        params.putHeaders(CACHE_CONTROL, CACHE_CONTROL_VALUE);
        params.putHeaders(CONNECTION, CONNECTION_VALUE);
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);
        params.putHeaders(REFERER, CSMY_EDUCATION_HOME);

        params.put(LOGIN_VIEWSTATE, LOGIN_VIEWSTATE_VALUE);
        params.put(LOGIN_VIEWSTATEGENERATOR, LOGIN_SCHEDULE_VIEWSTATEGENERATOR_VALUE);
        params.put(LOGIN_TEXT_BOX_ONE, EduInfo.getEducationUserName());
        params.put(LOGIN_TEXT_BOX_TWO, EduInfo.getEducationPassword());
        params.put(DDL_JS, DDL_JS_VALUE);
        params.put(BUTTON_ONE, BUTTON_ONE_VALUE);

        new RxVolley.Builder()
                .url(CSMY_EDUCATION_HOME)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .contentType(RxVolley.ContentType.FORM)
                .callback(new HttpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        Logger.d("登录教务系统成功");

                        enterEducationInterior();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        Logger.d("登录教务系统失败,code:" + errorNo);

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
                            case EventTag.QUERY_GRADE_GPA:
                                Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_FAIL);
                                break;
                            default:
                                break;
                        }

                    }
                })
                .doTask();
    }


    private static void enterEducationInterior() {
        Logger.d("RxVolley开始进入教务系统内部。。。");

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, CSMY_EDUCATION_HOME);
        params.putHeaders(ACCEPT, ACCEPT_VALUE);
        params.putHeaders(ACCEPT_ENCODING, ACCEPT_ENCODING_INTERIOR_VALUE);
        params.putHeaders(ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE);
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                Logger.d("进入教务系统内部成功");
                switch (EventTag.getCurrentTag()) {
                    case EventTag.LOGIN_EDUCATION:
                        obtainPersonalInfo();
                        break;
                    case EventTag.SWITCH_SCHEDULE:
                        switchSchedule(true);
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
                    case EventTag.QUERY_GRADE_GPA:
                        enterGradePage();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("进入教务系统内部失败,code is:" + errorNo+","+strMsg);
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
                    case EventTag.QUERY_GRADE_GPA:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_FAIL);
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
                .encoding(GB2312)
                .timeout(10000)
                .callback(httpCallback)
                .doTask();
    }


    public static void obtainSchedule() {
        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("获取课表成功:" + body);
                    //保存课表html
                    EduSchedule.saveScheduleHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                Logger.d("获取课表失败,code is:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_LOGIN_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO)
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .useServerControl(true)
                .encoding(GB2312)
                .timeout(10000)
                .callback(httpCallback)
                .doTask();
    }

    /**
     * 获取个人信息
     */
    public static void obtainPersonalInfo() {

        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);


        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("获取个人信息成功");
                    EduLogin.savePersonalInfoHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                Event.sendEmptyMessage(Event.EDUCATION_LOGIN_FAIL);
                Logger.d("获取个人信息失败:" + errorNo);
            }
        };

        new RxVolley.Builder()
                .url(CSMY_BASE_URL_PERSONAL_INFO_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_PERSONAL_INFO_PART_TWO)
                .httpMethod(RxVolley.Method.GET)
                .params(params)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 选择课表
     *
     * @param isXnd __EVENTTARGET 请求参数有两种，xqd和xnd.首先查询xnd，结果为0再查询xqd
     */
    public static void switchSchedule(final boolean isXnd) {


        String eventTarget;
        String viewState;
        if (isXnd) {
            eventTarget = SCHEDULE_XND;
            viewState = SCHEDULE_XND_VIEWSTATE;
        } else {
            eventTarget = SCHEDULE_XQD;
            viewState = SCHEDULE_XQD_VIEWSTATE;
        }
        HttpParams params = new HttpParams();
        params.put(EVENTTARGET, eventTarget);
        params.put(EVENTARGUMENT, EMPTY);
        params.put(VIEWSTATE, viewState);
        params.put(VIEWSTATEGENERATOR, SCHEDULE_VIEWSTATEGENERATOR_VALUE);
        params.put(SCHEDULE_XND, EduInfo.getCurrentAcademicYear());
        params.put(SCHEDULE_XQD, EduInfo.getCurrentTerm());
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);


        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    if (body.contains(NOT_COMMON)) {
                        Event.sendEmptyMessage(Event.EDUCATION_SWITCH_SCHEDULE_FAIL);
                        Logger.d("选择课表失败：" + NOT_COMMON);
                    } else {
                        Logger.d("选择课表成功");
                        EduSchedule.saveScheduleHtml(body);
                        List<Course> courseList = EduSchedule.getEducationScheduleCourses(true);
                        if (courseList.size() == 0) {
                            if (isXnd) {
                                switchSchedule(false);
                                return;
                            } else {
                                Event.sendEmptyMessage(Event.SWITCH_SCHEDULE_NO_COURSE);
                                return;
                            }
                        }
                        Logger.d("有多少门课：" + courseList.size());
                        DataSupport.deleteAll(Course.class, SWITCH_SCHEDULE_DELETE_WHERE, EduInfo.getCurrentAcademicYear(), EduInfo.getCurrentTerm());
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
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 进入成绩查询页面
     */
    public static void enterGradePage() {
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);


        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("进入成绩查询页面成功");
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
                        case EventTag.QUERY_GRADE_GPA:
                            queryGradeGpa();
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
                Logger.d("成绩查询失败:" + errorNo);
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
                    case EventTag.QUERY_GRADE_GPA:
                        Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_FAIL);
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
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }

    /**
     * 查询成绩
     */
    public static void queryGrade() {


        String view_state = EduGrade.getViewState();
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.put(EVENTTARGET, EMPTY);
        params.put(EVENTARGUMENT, EMPTY);
        params.put(VIEWSTATE, view_state);
        params.put(VIEWSTATEGENERATOR, GRADE_VIEWSTATEGENERATOR_VALUE);
        params.put(DDL_XN, EduGrade.getGradeAcadamicYear());
        params.put(DDL_XQ, EduGrade.getGradeTerm());
        params.put(DDL_KCXZ, EMPTY);
        params.put(BTN_XQ, BTN_XQ_VALUE);
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, url);
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);


        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("成绩查询成功");
                    EduGrade.saveGradeHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("成绩查询失败:" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }


    /**
     * 成绩统计
     */
    public static void gradeStatistical() {
        String view_state = EduGrade.getViewState();
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.put(EVENTTARGET, EMPTY);
        params.put(EVENTARGUMENT, EMPTY);
        params.put(VIEWSTATE, view_state);
        params.put(VIEWSTATEGENERATOR, GRADE_VIEWSTATEGENERATOR_VALUE);
        params.put(DDL_XN, EMPTY);
        params.put(DDL_XQ, EMPTY);
        params.put(DDL_KCXZ, EMPTY);
        params.put(BUTTON_ONE, GRADE_STATISTICAL_BUTTON_ONE_VALUE);
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, url);
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("成绩统计查询成功");
                    EduGrade.saveGradeStatisticalHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("成绩统计查询失败" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_GRADE_STATISTICAL_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }


    /**
     * 查询绩点
     */
    public static void queryGradeGpa() {
        String view_state = EduGrade.getViewState();
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        HttpParams params = new HttpParams();
        params.put(EVENTTARGET, EMPTY);
        params.put(EVENTARGUMENT, EMPTY);
        params.put(VIEWSTATE, view_state);
        params.put(VIEWSTATEGENERATOR, GRADE_VIEWSTATEGENERATOR_VALUE);
        params.put(DDL_XN, EduGrade.getGpaAcadamicYear());
        params.put(DDL_XQ, EduGrade.getGpaTerm());
        params.put(DDL_KCXZ, EMPTY);
        params.put(BUTTON_ONE, GRADE_STATISTICAL_BUTTON_ONE_VALUE);
        params.putHeaders(COOKIE, EduInfo.getCookie());
        params.putHeaders(REFERER, url);
        params.putHeaders(USER_AGENT, USER_AGENT_CHROME);

        HttpCallback httpCallback = new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                try {
                    String body = new String(t, GB2312);
                    Logger.d("绩点查询成功");
                    EduGrade.saveGradeStatisticalHtml(body);
                    Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_SUCCESS);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("绩点查询失败" + errorNo);
                Event.sendEmptyMessage(Event.EDUCATION_QUERY_GRADE_GPA_FAIL);
            }
        };

        new RxVolley.Builder()
                .url(url)
                .httpMethod(RxVolley.Method.POST)
                .params(params)
                .encoding(GB2312)
                .timeout(10000)
                .useServerControl(true)
                .callback(httpCallback)
                .doTask();

    }
}
