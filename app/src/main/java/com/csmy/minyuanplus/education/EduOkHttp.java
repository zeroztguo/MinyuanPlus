package com.csmy.minyuanplus.education;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Zero on 16/7/13.
 */
public class EduOkHttp {
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

    public static void enterEducationHome() {
        OkHttpUtils
                .get()
                .url(CSMY_EDUCATION)
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return response.header("Set-Cookie");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("进入教务系统主页失败\n" + e.toString());

                    }

                    @Override
                    public void onResponse(Object response) {
                        Logger.d("进入教务系统主页成功，cookie：" + response.toString());
                        EduInfo.saveCookie(response.toString());
                        loignEducation2();
                    }
                });
    }

    public static void loignEducation() {
        OkHttpUtils
                .post()
                .url(CSMY_EDUCATION_HOME)
                .addHeader(COOKIE, EduInfo.getCookie())
                .addHeader(CONTENT_TYPE,"application/x-www-form-urlencoded")
                .addParams("__VIEWSTATE", "dDw3OTkxMjIwNTU7Oz4BO%2FY3htAyQJF%2FNwKj5qZqDUYy9g%3D%3D")
                .addParams("__VIEWSTATEGENERATOR", "92719903")
                .addParams("TextBox1", EduInfo.getEducationUserName())
                .addParams("TextBox2", EduInfo.getEducationPassword())
                .addParams("TextBox3", "")
                .addParams("RadioButtonList1", "%D1%A7%C9%FA")
                .addParams("Button1", "")
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return response.body().bytes() + "";
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d(e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.d("登录成功,code:\n" + response);
                    }
                });

        Logger.d("开始登陆111111。。。");
    }

    public static void loignEducation2() {
        OkHttpUtils
                .postString()
                .url(CSMY_EDUCATION_HOME)
                .addHeader(COOKIE, EduInfo.getCookie())
                .addHeader(CONTENT_TYPE,"application/x-www-form-urlencoded")
//                .addHeader("Host",CSMY_EDUCATION)
//                .addHeader("Cache-Control","max-age=0")
//                .addHeader("Origin",CSMY_EDUCATION)
//                .addHeader("Upgrade-Insecure-Requests","1")
//                .addHeader("Content-Type","application/x-www-form-urlencoded")
//                .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                .addHeader(REFERER,CSMY_EDUCATION_HOME)
//                .addHeader("Accept-Encoding","gzip, deflate")
//                .addHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6")
                .content("__VIEWSTATE=dDw3OTkxMjIwNTU7Oz4BO%2FY3htAyQJF%2FNwKj5qZqDUYy9g%3D%3D&__VIEWSTATEGENERATOR=92719903&TextBox1=1425113212&TextBox2=k147258&TextBox3=&RadioButtonList1=%D1%A7%C9%FA&Button1=")
//                .addParams("__VIEWSTATE","dDwtMTM2MTgxNTk4OTs7Pof5kqHpeaUSQgt4AgvDD0O%2F1ELE")
//                .addParams("__VIEWSTATEGENERATOR","94A5C162")
//                .addParams("TextBox1",EduInfo.getEducationUserName())
//                .addParams("TextBox2",EduInfo.getEducationPassword())
//                .addParams("ddl_js","%D1%A7%C9%FA")
//                .addParams("Button1","+%B5%C7+%C2%BC+")
                .build()
                .execute(new Callback<String>() {
                    @Override
                    public String parseNetworkResponse(Response response) throws Exception {
                        return response.code()+response.body().string()+"";
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d(e.toString());
//                        if(e.toString().contains("302")){
//                            enterEducationInterior();
//                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.d("登录成功:"+response.length() );
//                        Log.i(TAG, response);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < response.length(); i+=50) {
                            if(i>response.length()||i+20>response.length())
                                break;
//                            sb.append(response.substring(i,i+20));
                            Log.i(TAG, response.substring(i,i+50));
                        }
//                        Logger.d(sb.toString());
                        enterEducationInterior();
                    }
                });

        Logger.d("开始登陆。。。22222");
        Log.i(TAG, "测试Log是否可用");
    }


    public static void enterEducationInterior() {
        OkHttpUtils
                .get()
                .url(CSMY_BASE_URL_XH + EduInfo.getEducationUserName())
                .addHeader(COOKIE, EduInfo.getCookie())
                .addHeader(CONTENT_TYPE,"application/x-www-form-urlencoded")
                .build()
                .execute(new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return response.body().string()+"";
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("进入系统内部：" + e.toString());
                    }

                    @Override
                    public void onResponse(Object response) {
                        Logger.d("进入系统内部：" + response.toString());
                        Logger.xml(response.toString());
                    }
                });

//        new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e) {
//                Logger.d("进入教务系统内部失败\n"+e.toString());
//                if(e.toString().contains("302")){
//                    obtainSchedule();
//                }
//
//            }
//
//            @Override
//            public void onResponse(String response) {
//                Logger.d("进入教务系统内部成功\n"+response);
//                obtainSchedule();
//            }
//        }
    }


    public static void obtainSchedule() {
        OkHttpUtils
                .get()
                .url(CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO)
                .addHeader(COOKIE, EduInfo.getCookie())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("获取课表失败\n" + e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.d("获取课表成功\n" + response);
                    }
                });

    }

    public static void obtainPersonalInfo() {
        OkHttpUtils
                .get()
                .url(CSMY_BASE_URL_PERSONAL_INFO_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_PERSONAL_INFO_PART_TWO)
                .addHeader(COOKIE, EduInfo.getCookie())
                .addHeader(REFERER, CSMY_REFERER_URL + EduInfo.getEducationUserName())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("获取课表失败\n" + e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.d("获取课表成功\n" + response);
                    }
                });
    }

    public static void switchSchedule(String academicYear, String term) {
        OkHttpUtils
                .post()
                .url(CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO)
                .addParams("__EVENTTARGET", "xqd")
                .addParams("__EVENTARGUMENT", "")
                .addParams("__VIEWSTATE", "dDwxOTA3MTE1Njc2O3Q8cDxsPHhoO2R5YnlzY2o7enhjamN4eHM7c2ZkY2JrO1NvcnRFeHByZXM7U29ydERpcmU7ZGczO3N0cl90YWJfYmpnOz47bDwxNDI1MTEzMjEyO1xlOzA7XGU7a2NtYzthc2M7YmpnO3pmX2N4Y2p0al8xNDI1MTEzMjEyOz4%2BO2w8aTwxPjs%2BO2w8dDw7bDxpPDI%2BO2k8Nz47aTwyMj47aTwyNj47aTwyOD47aTwzMD47aTwzMj47aTwzMz47aTwzNT47aTwzNz47aTwzOT47aTw0MT47aTw0Mz47aTw0NT47aTw0Nz47aTw0OT47aTw1MT47aTw1Mz47aTw1ND47aTw1Nj47aTw1OD47aTw2MD47PjtsPHQ8dDw7dDxpPDE3PjtAPFxlOzIwMDEtMjAwMjsyMDAyLTIwMDM7MjAwMy0yMDA0OzIwMDQtMjAwNTsyMDA1LTIwMDY7MjAwNi0yMDA3OzIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzs%2BO0A8XGU7MjAwMS0yMDAyOzIwMDItMjAwMzsyMDAzLTIwMDQ7MjAwNC0yMDA1OzIwMDUtMjAwNjsyMDA2LTIwMDc7MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7MjAxMy0yMDE0OzIwMTQtMjAxNTsyMDE1LTIwMTY7MjAxNi0yMDE3Oz4%2BOz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGtjeHptYztrY3h6ZG07Pj47Pjt0PGk8MTI%2BO0A85YWs5YWx5b%2BF5L%2Bu6K%2B%2BO%2BWFrOWFseS7u%2BmAieivvjvkuJPkuJrku7vpgInor7475LiT5Lia576k5b%2BF5L%2Bu6K%2B%2BO%2BS4k%2BS4mumZkOmAieivvjvntKDotKjmi5PlsZU75LiT5Lia576k5LqS6YCJO%2BWFrOWFseWfuuehgOivvjvkuJPkuJrmlrnlkJHor7475LiT5Lia576k5Z%2B656GA6K%2B%2BO%2BS4k%2BS4muW%2FheS%2FruivvjtcZTs%2BO0A8MDE7MDI7MDM7MDQ7MDU7MDY7MDc7MDg7MDk7MTA7MTE7XGU7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w85a2m5Y%2B377yaMTQyNTExMzIxMjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWnk%2BWQje%2B8mumDreazvea2mztvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOS4k%2BS4mu%2B8mjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOi9r%2BS7tuaKgOacr%2B%2B8iOenu%2BWKqOW6lOeUqOW8gOWPkeaWueWQke%2B8iTtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOihjOaUv%2BePre%2B8muenu%2BWKqDE0MzI7bzx0Pjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjs%2BO2w8dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO3A8bDxzdHlsZTs%2BO2w8RElTUExBWTpub25lOz4%2BPjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDzmnKzkuJPkuJrlhbExNDXkuro7bzxmPjs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjs%2BO2w8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BOz47bDx0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8U1VFUzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8SW1hZ2VVcmw7PjtsPC4vZXhjZWwvOTcwNTI3MC5qcGc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w86Iez5LuK5pyq6YCa6L%2BH6K%2B%2B56iL5oiQ57up77yaO288dD47Pj47Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6YmxvY2s7Pj4%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjtsPGNrX2R5Oz4%2BXZUjpstynTgGKTsJaNPNUY2GmeU%3D")
                .addParams("__VIEWSTATEGENERATOR", "9727EB43")
                .addParams("ddlXN", academicYear)
                .addParams("ddlXQ", term)
                .addParams("ddl_kcxz", "")
                .addParams("btn_xq", "%D1%A7%C6%DA%B3%C9%BC%A8")
                .addHeader(COOKIE, EduInfo.getCookie())
                .addHeader(REFERER, CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("选择课表失败\n" + e.toString());
                    }

                    @Override
                    public void onResponse(String response) {
                        Logger.d("选择课表成功\n" + response);
                    }
                });

    }


    private void obtainCollegeNews() {
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "9")
                .addParams("v1", "104815")
                .addParams("tempData", new Date().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Elements es = document.select("span");
                        for (Element e : es) {
//                            Log.i(TAG, e.text());
                        }
//                        mTV.setText(es.toString());
                    }
                });
    }

}
