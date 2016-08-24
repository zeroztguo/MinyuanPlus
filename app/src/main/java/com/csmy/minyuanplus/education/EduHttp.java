package com.csmy.minyuanplus.education;

import android.util.Log;

import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventMsg;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.support.util.Util;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zero on 16/5/28.
 */
public class EduHttp extends Util {
    public static final String CSMY_EDUCATION = "http://jw3.csmzxy.com:8088";
    public static final String CSMY_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String CSMY_EDUCATION_HOME = "http://jw3.csmzxy.com:8088/default2.aspx";
    public static final String CSMY_BASE_URL_XH = "http://jw3.csmzxy.com:8088/xs_main.aspx?xh=";
    public static final String CSMY_BASE_URL_SCHEDULE_PART_ONE = "http://jw3.csmzxy.com:8088/xskbcx.aspx?xh=";
    public static final String CSMY_BASE_URL_SCHEDULE_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121603";
    public static final String CSMY_BASE_URL_PERSONAL_INFO_PART_ONE = "http://jw3.csmzxy.com:8088/xsgrxx.aspx?xh=";
    public static final String CSMY_BASE_URL_PERSONAL_INFO_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121501";
    public static final String CSMY_BASE_URL_GRADE_HOME_PART_ONE = "http://jw3.csmzxy.com:8088/xscjcx.aspx?xh=";
    public static final String CSMY_BASE_URL_GRADE_HOME_PART_TWO = "&xm=%B9%F9%D4%F3%CC%CE&gnmkdm=N121605";
    public static String CSMY_REFERER_URL = "http://jw3.csmzxy.com:8088/xs_main.aspx?xh=";

    public static final String COOKIE = "Cookie";
    public static final String REFERER = "Referer";
    public static final String ORIGIN = "Origin";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    
    public static final int EDUCATION_SCHEDULE = 0;
    public static final int EDUCATION_PERSONAL_INFO = 1;
    public static final int EDUCATION_GRADE = 2;
    public static final int EDUCATION_LOGIN_ERROR = 3;
    public static final int EDUCATION_PASSWORD_ERROR = 4;

    private static final String TAG = "HttpUtil";
    public static final String NOT_COMMON = "你还没有进行本学期的教学质量评价";


    /**
     * @param url          地址
     * @param isSaveCookie 是否保存cookie，是则保存，否则提交
     * @param referer      如果为空则不发送
     * @param callBack     回调
     */
    public static void get(String url, final boolean isSaveCookie, final String referer, final MyHttpCallBack callBack) {
        final String address = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream in = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    if (!isSaveCookie) {
                        connection.setRequestProperty(COOKIE, EduInfo.getCookie());
                    }
                    if (!referer.isEmpty()) {
                        connection.setRequestProperty(REFERER, referer);
                    }
//                    connection.connect();
                    in = connection.getInputStream();

                    //成功
                    Map<String, String> headers = new HashMap<String, String>();
                    int respCode = connection.getResponseCode();
                    if (respCode <= 302) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(in, "gb2312")
                        );
                        StringBuffer response = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line + "\n");
                        }
                        headers.put(CONTENT_LENGTH, connection.getContentLength() + "");
                        if (isSaveCookie) {
                            String cookie = connection.getHeaderField("Set-Cookie");
                            //保存cookie
                            EduInfo.saveCookie(cookie);
                            EduInfo.saveObtainCookieDate();
                        }
                        callBack.onSuccess(headers, respCode, response.toString());
                    } else {
                        callBack.onFailure(headers, respCode);
                    }
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    callBack.onFailure("连接服务器超时");
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null)
                            in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * @param url      地址
     * @param params   参数(头信息会跟根据参数名自动添加)
     * @param callBack 回调
     */
    private static void post(String url, final Map<String, String> params, final MyHttpCallBack callBack) {
        final String address = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream in = null;
                OutputStream out = null;
                //要传输的数据
                StringBuilder param = new StringBuilder();
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
//                    connection.setInstanceFollowRedirects(false);

                    Set keySet = params.keySet();
                    for (Object key : keySet) {
                        String value = params.get(key);
                        if (key.toString().equals(COOKIE)) {
                            connection.setRequestProperty(COOKIE, value);
                        } else if (key.toString().equals(REFERER)) {
                            connection.setRequestProperty(REFERER, value);
                        } else if (key.toString().equals(ORIGIN)) {
                            connection.setRequestProperty(ORIGIN, value);
                        }else if (key.toString().equals(CONTENT_TYPE)) {
                            connection.setRequestProperty(CONTENT_TYPE, value);
                        } else {
                            param.append(key.toString() + "=" + value + "&");
                        }
                    }


                    String paramStr = param.toString().substring(0, param.length() - 1);
                    Logger.d(paramStr + "\n" + connection.getURL() + "\n" + connection.getRequestProperties().toString());

//                    connection.connect();

                    //获取输出流
                    out = connection.getOutputStream();
                    out.write(paramStr.getBytes());
                    out.flush();



                    int respCode = connection.getResponseCode();

                    Map<String, String> headers = new HashMap<String, String>();
                    if (respCode <= 302) {
                        in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(in, "gb2312")
                        );


                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        headers.put(CONTENT_LENGTH, connection.getContentLength() + "");


                        callBack.onSuccess(headers, respCode, response.toString());

                    } else {
                        in = connection.getErrorStream();
                        callBack.onFailure(headers, respCode);
                    }

                } catch (MalformedURLException e) {
                    callBack.onFailure("连接服务器超时");
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                    try {
                        if (in != null)
                            in.close();
                        if (out != null)
                            out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    //------------------------- ------------------------------------------------------------

    /**
     * 进入教务系统主页
     */
    public static void enterEducationHome() {
        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                Logger.d("进入教务系统主页成功" + EduInfo.getCookie() + "\n" + response);
                login();
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("进入教务系统主页失败");
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_FAIL, EventMsg.CONNECT_SERVER_TIME_OUT));
            }
        };
        get(CSMY_EDUCATION_HOME, true, "", callBack);
    }


    /**
     * 登录
     */
    public static void login() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("__VIEWSTATE", "ddDw3OTkxMjIwNTU7Oz58Am18OJsuH88mdy%2BcryX%2FbTd2Gw%3D%3D");
        params.put("__VIEWSTATEGENERATOR", "92719903");
        params.put("TextBox1", EduInfo.getEducationUserName());
        params.put("TextBox2", EduInfo.getEducationPassword());
        params.put("TextBox3", "");
        params.put("RadioButtonList1", "%D1%A7%C9%FA");
        params.put("Button1", "");
        params.put(COOKIE, EduInfo.getCookie());


        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                enterEducationInterior();
//                    Logger.d(respCode +" "+headers.get(CONTENT_LENGTH)+" "+response+LoginUtil.getEducationUserName()+" "+LoginUtil.getEducationPassword()+ " "+LoginUtil.getCookie());
                Logger.d("登录验证 ing。。。"+respCode+" Content-Type:"+headers.get(CONTENT_TYPE)+response);
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("登录失败，响应吗："+respCode);
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_FAIL, EventMsg.CONNECT_SERVER_TIME_OUT));
            }
        };
        post(CSMY_EDUCATION_HOME, params, callBack);
    }

    /**
     * 进入教务系统内部
     */
    public static void enterEducationInterior() {
        String url = CSMY_BASE_URL_XH + EduInfo.getEducationUserName();
        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
//                if (Integer.valueOf(headers.get(CONTENT_LENGTH)) > 0) {
                    Logger.d("登录成功");
                    Logger.d("进入教务内部成功:" + respCode + " " + headers.get(CONTENT_LENGTH) + " ");
//                } else {
//                    Logger.d("登录失败，密码错误 响应码："+respCode+" Content-Length:"+headers.get(CONTENT_LENGTH));
//                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_FAIL, EventMsg.LOGIN_INFO_ERROR));
//                }
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("进入教务内部失败，服务器问题"+respCode);
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_FAIL, EventMsg.CONNECT_SERVER_TIME_OUT));
            }

            @Override
            public void onFailure(String error) {
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_FAIL, EventMsg.CONNECT_SERVER_TIME_OUT));
            }
        };
        get(url, false, "", callBack);

    }

    /**
     * 获取课表
     */
    public static void obtainSchedule() {
        String url = CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO;
        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                if (response.contains(NOT_COMMON)) {
                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_SCHEDULE_FAIL, EventMsg.NEED_COMMON));
                } else {
                    EduSchedule.saveScheduleHtml(response);
                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS));
                }
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("获取课表失败");
            }
        };
        get(url, false, CSMY_REFERER_URL + EduInfo.getEducationUserName(), callBack);
    }

    /**
     * 获取个人信息
     */
    public static void obtainPersonalInfo() {
        String url = CSMY_BASE_URL_PERSONAL_INFO_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_PERSONAL_INFO_PART_TWO;
        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                Logger.d("获取个人信息成功:" + respCode + " " + headers.get(CONTENT_LENGTH) + " ");
                EduLogin.savePersonalInfoHtml(response.toString());
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS));
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("获取个人信息失败");
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL));
            }
        };
        get(url, false, CSMY_REFERER_URL + EduInfo.getEducationUserName(), callBack);
    }

    /**
     * 选择课表
     * @param academicYear 学年
     * @param term 学期
     */
    public static void switchSchedule(String academicYear, String term) {
        String url = CSMY_BASE_URL_SCHEDULE_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_SCHEDULE_PART_TWO;

        Map<String, String> params = new HashMap<String, String>();
        params.put("__EVENTTARGET", "xqd");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", "dDwtODAxODI2NDQzO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw5Pjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDM%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8ND47QDwyMDE2LTIwMTc7MjAxNS0yMDE2OzIwMTQtMjAxNTtcZTs%2BO0A8MjAxNi0yMDE3OzIwMTUtMjAxNjsyMDE0LTIwMTU7XGU7Pj47bDxpPDE%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwxPjs%2BPjs7Pjt0PDtsPGk8MD47PjtsPHQ8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47O2w8aTwwPjs%2BPjs7Pjs%2BPjs%2BPjs%2BPjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BOz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bmo7bmo7Pj47Pjt0PGk8MTU%2BO0A8MjAxNjsyMDE1OzIwMTQ7MjAxMzsyMDEyOzIwMTE7MjAxMDsyMDA5OzIwMDg7MjAwNzsyMDA2OzIwMDU7MjAwNDsyMDAzO1xlOz47QDwyMDE2OzIwMTU7MjAxNDsyMDEzOzIwMTI7MjAxMTsyMDEwOzIwMDk7MjAwODsyMDA3OzIwMDY7MjAwNTsyMDA0OzIwMDM7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4eW1jO3h5ZG07Pj47Pjt0PGk8MTE%2BO0A85ZWG5a2m6ZmiO%2BeUteWtkOS%2FoeaBr%2BW3peeoi%2BWtpumZojvnpL7kvJrnrqHnkIblrabpmaI756S%2B5Lya5bel5L2c5a2m6ZmiO%2BauoeS7quWtpumZojvoibrmnK%2FlrabpmaI75paH5YyW5Lyg5pKt5a2m6ZmiO%2Bi9r%2BS7tuWtpumZojvljLvlrabpmaI75aSW6K%2Bt5a2m6ZmiO1xlOz47QDwxODsxOTsyMDsyMTsyMjsyMzsyNDsyNTsyNjsyNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHp5bWM7enlkbTs%2BPjs%2BO3Q8aTw4PjtAPOeUteWtkOWVhuWKoTvlm73pmYXnu4%2FmtY7kuI7otLjmmJM75Lya6K6hO%2BW4guWcuuiQpemUgDvnianmtYHnrqHnkIY76K%2BB5Yi45oqV6LWE5LiO566h55CGO%2BivgeWIuOS4juacn%2Bi0pztcZTs%2BO0A8MTgwNTsxODAzOzE4MDE7MTgwMjsxODA2OzE4MDQ7MTgwNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGJqbWM7YmpkbTs%2BPjs%2BO3Q8aTw0PjtAPOeUteWVhjE2MzM755S15ZWGMTYzMTvnlLXllYYxNjMyO1xlOz47QDwxNjE4MDUzMzsxNjE4MDUzMTsxNjE4MDUzMjtcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhtO3hoOz4%2BOz47dDxpPDE%2BO0A8XGU7PjtAPFxlOz4%2BOz47Oz47Pj47Pj47dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwyPjtpPDQ%2BO2k8Nj47aTw4PjtpPDExPjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNDI1MTEzMjEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpg63ms73mtps7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia77ya6L2v5Lu25oqA5pyv77yI56e75Yqo5bqU55So5byA5Y%2BR5pa55ZCR77yJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrnp7vliqgxNDMyOz4%2BOz47Oz47dDx0PDs7bDxpPDA%2BOz4%2BOzs%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwxPjtpPDE%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47bDxpPDA%2BOz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia5rex5bqm56S%2B5Lya5a6e6Le1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzliJjljZPlpoI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEuMDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MDItMTk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MT47aTwxPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDE%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwyMDE1LTIwMTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4k%2BS4mua3seW6puekvuS8muWunui3tTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85YiY5Y2T5aaCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxLjA7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjs%2BPjs%2B8U%2FrZesVxymifx8EKT7RuFOUgVc%3D");
        params.put("__VIEWSTATEGENERATOR", "55530A43");
        params.put("xnd", academicYear);
        params.put("xqd", term);
        params.put(COOKIE, EduInfo.getCookie());
        params.put(REFERER, url);

        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                if (response.contains(NOT_COMMON)) {
                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_SWITCH_SCHEDULE_FAIL, EventMsg.NEED_COMMON));
                } else {
                    Logger.d("获取课表成功:" + respCode + " " + headers.get(CONTENT_LENGTH) + " ");
                    EduSchedule.saveScheduleHtml(response);
                    List<Course> courseList = EduSchedule.getEducationScheduleCourses(false);
                    DataSupport.saveAll(courseList);
                    EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS));
                }

            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("获取课表失败");
            }
        };
        post(url, params, callBack);
    }

    /**
     * 查询成绩
     * @param academicYear 学年
     * @param term 学期
     */
    public static void queryGrade(String academicYear, String term) {
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;
        Log.d(TAG, "url:"+url+"\n"+academicYear+" "+term);

        Map<String, String> params = new HashMap<String, String>();

        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", "dDwxOTA3MTE1Njc2O3Q8cDxsPHhoO2R5YnlzY2o7enhjamN4eHM7c2ZkY2JrO1NvcnRFeHByZXM7U29ydERpcmU7ZGczO3N0cl90YWJfYmpnOz47bDwxNDI1MTEzMjEyO1xlOzA7XGU7a2NtYzthc2M7YmpnO3pmX2N4Y2p0al8xNDI1MTEzMjEyOz4%2BO2w8aTwxPjs%2BO2w8dDw7bDxpPDI%2BO2k8Nz47aTwyMj47aTwyNj47aTwyOD47aTwzMD47aTwzMj47aTwzMz47aTwzNT47aTwzNz47aTwzOT47aTw0MT47aTw0Mz47aTw0NT47aTw0Nz47aTw0OT47aTw1MT47aTw1Mz47aTw1ND47aTw1Nj47aTw1OD47aTw2MD47PjtsPHQ8dDw7dDxpPDE3PjtAPFxlOzIwMDEtMjAwMjsyMDAyLTIwMDM7MjAwMy0yMDA0OzIwMDQtMjAwNTsyMDA1LTIwMDY7MjAwNi0yMDA3OzIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzs%2BO0A8XGU7MjAwMS0yMDAyOzIwMDItMjAwMzsyMDAzLTIwMDQ7MjAwNC0yMDA1OzIwMDUtMjAwNjsyMDA2LTIwMDc7MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7MjAxMy0yMDE0OzIwMTQtMjAxNTsyMDE1LTIwMTY7MjAxNi0yMDE3Oz4%2BOz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGtjeHptYztrY3h6ZG07Pj47Pjt0PGk8MTI%2BO0A85YWs5YWx5b%2BF5L%2Bu6K%2B%2BO%2BWFrOWFseS7u%2BmAieivvjvkuJPkuJrku7vpgInor7475LiT5Lia576k5b%2BF5L%2Bu6K%2B%2BO%2BS4k%2BS4mumZkOmAieivvjvntKDotKjmi5PlsZU75LiT5Lia576k5LqS6YCJO%2BWFrOWFseWfuuehgOivvjvkuJPkuJrmlrnlkJHor7475LiT5Lia576k5Z%2B656GA6K%2B%2BO%2BS4k%2BS4muW%2FheS%2FruivvjtcZTs%2BO0A8MDE7MDI7MDM7MDQ7MDU7MDY7MDc7MDg7MDk7MTA7MTE7XGU7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w85a2m5Y%2B377yaMTQyNTExMzIxMjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWnk%2BWQje%2B8mumDreazvea2mztvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOS4k%2BS4mu%2B8mjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOi9r%2BS7tuaKgOacr%2B%2B8iOenu%2BWKqOW6lOeUqOW8gOWPkeaWueWQke%2B8iTtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOihjOaUv%2BePre%2B8muenu%2BWKqDE0MzI7bzx0Pjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjs%2BO2w8dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO3A8bDxzdHlsZTs%2BO2w8RElTUExBWTpub25lOz4%2BPjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDzmnKzkuJPkuJrlhbExNDXkuro7bzxmPjs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjs%2BO2w8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BOz47bDx0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8U1VFUzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8SW1hZ2VVcmw7PjtsPC4vZXhjZWwvOTcwNTI3MC5qcGc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w86Iez5LuK5pyq6YCa6L%2BH6K%2B%2B56iL5oiQ57up77yaO288dD47Pj47Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6YmxvY2s7Pj4%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjtsPGNrX2R5Oz4%2BXZUjpstynTgGKTsJaNPNUY2GmeU%3D");
        params.put("__VIEWSTATEGENERATOR", "9727EB43");
        params.put("ddlXN", academicYear);
        params.put("ddlXQ", term);
        params.put("ddl_kcxz", "");
        params.put("btn_xq", "%D1%A7%C6%DA%B3%C9%BC%A8");
        params.put(COOKIE, EduInfo.getCookie());
        params.put(REFERER, url);
//        params.put(ORIGIN, CSMY_EDUCATION);
//        params.put(CONTENT_TYPE, CSMY_CONTENT_TYPE);

        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                Logger.d("成绩查询成功:" + respCode + " " + headers.get(CONTENT_LENGTH) + " ");
                EduGrade.saveGradeHtml(response);
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_QUERY_GRADE_SUCCESS));
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("成绩查询失败");
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_QUERY_GRADE_FAIL));
            }
        };
        post(url, params, callBack);
    }

    /**
     * 查询未通过成绩
     */
    public static void queryFailGrade(){


    }

    /**
     * 成绩统计
     */
    public static void gradeStatistical(){
        String url = CSMY_BASE_URL_GRADE_HOME_PART_ONE + EduInfo.getEducationUserName() + CSMY_BASE_URL_GRADE_HOME_PART_TWO;

        Map<String, String> params = new HashMap<String, String>();

        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", "dDwxOTA3MTE1Njc2O3Q8cDxsPHhoO2R5YnlzY2o7enhjamN4eHM7c2ZkY2JrO1NvcnRFeHByZXM7U29ydERpcmU7ZGczO3N0cl90YWJfYmpnOz47bDwxNDI1MTEzMjEyO1xlOzA7XGU7a2NtYzthc2M7YmpnO3pmX2N4Y2p0al8xNDI1MTEzMjEyOz4%2BO2w8aTwxPjs%2BO2w8dDw7bDxpPDI%2BO2k8Nz47aTwyMj47aTwyNj47aTwyOD47aTwzMD47aTwzMj47aTwzMz47aTwzNT47aTwzNz47aTwzOT47aTw0MT47aTw0Mz47aTw0NT47aTw0Nz47aTw0OT47aTw1MT47aTw1Mz47aTw1ND47aTw1Nj47aTw1OD47aTw2MD47PjtsPHQ8dDw7dDxpPDE3PjtAPFxlOzIwMDEtMjAwMjsyMDAyLTIwMDM7MjAwMy0yMDA0OzIwMDQtMjAwNTsyMDA1LTIwMDY7MjAwNi0yMDA3OzIwMDctMjAwODsyMDA4LTIwMDk7MjAwOS0yMDEwOzIwMTAtMjAxMTsyMDExLTIwMTI7MjAxMi0yMDEzOzIwMTMtMjAxNDsyMDE0LTIwMTU7MjAxNS0yMDE2OzIwMTYtMjAxNzs%2BO0A8XGU7MjAwMS0yMDAyOzIwMDItMjAwMzsyMDAzLTIwMDQ7MjAwNC0yMDA1OzIwMDUtMjAwNjsyMDA2LTIwMDc7MjAwNy0yMDA4OzIwMDgtMjAwOTsyMDA5LTIwMTA7MjAxMC0yMDExOzIwMTEtMjAxMjsyMDEyLTIwMTM7MjAxMy0yMDE0OzIwMTQtMjAxNTsyMDE1LTIwMTY7MjAxNi0yMDE3Oz4%2BOz47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGtjeHptYztrY3h6ZG07Pj47Pjt0PGk8MTI%2BO0A85YWs5YWx5b%2BF5L%2Bu6K%2B%2BO%2BWFrOWFseS7u%2BmAieivvjvkuJPkuJrku7vpgInor7475LiT5Lia576k5b%2BF5L%2Bu6K%2B%2BO%2BS4k%2BS4mumZkOmAieivvjvntKDotKjmi5PlsZU75LiT5Lia576k5LqS6YCJO%2BWFrOWFseWfuuehgOivvjvkuJPkuJrmlrnlkJHor7475LiT5Lia576k5Z%2B656GA6K%2B%2BO%2BS4k%2BS4muW%2FheS%2FruivvjtcZTs%2BO0A8MDE7MDI7MDM7MDQ7MDU7MDY7MDc7MDg7MDk7MTA7MTE7XGU7Pj47Pjs7Pjt0PHA8cDxsPFZpc2libGU7PjtsPG88dD47Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w85a2m5Y%2B377yaMTQyNTExMzIxMjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWnk%2BWQje%2B8mumDreazvea2mztvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOS4k%2BS4mu%2B8mjtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOi9r%2BS7tuaKgOacr%2B%2B8iOenu%2BWKqOW6lOeUqOW8gOWPkeaWueWQke%2B8iTtvPHQ%2BOz4%2BOz47Oz47dDxwPHA8bDxUZXh0O1Zpc2libGU7PjtsPOihjOaUv%2BePre%2B8muenu%2BWKqDE0MzI7bzx0Pjs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjs%2BO2w8dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO3A8bDxzdHlsZTs%2BO2w8RElTUExBWTpub25lOz4%2BPjs7Ozs7Ozs7Ozs%2BOzs%2BO3Q8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjtpPDE%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6bm9uZTs%2BPj47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PEAwPDs7Ozs7Ozs7Ozs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BO2k8MT47aTwyPjtpPDM%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VGV4dDtWaXNpYmxlOz47bDzmnKzkuJPkuJrlhbExNDXkuro7bzxmPjs%2BPjs%2BOzs%2BOz4%2BO3Q8O2w8aTwwPjs%2BO2w8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Oz47Pj47dDw7bDxpPDA%2BOz47bDx0PHA8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47Pjs7Pjs%2BPjt0PDtsPGk8MD47PjtsPHQ8cDxwPGw8VmlzaWJsZTs%2BO2w8bzxmPjs%2BPjs%2BOzs%2BOz4%2BOz4%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8U1VFUzs%2BPjs%2BOzs%2BO3Q8cDxwPGw8SW1hZ2VVcmw7PjtsPC4vZXhjZWwvOTcwNTI3MC5qcGc7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7VmlzaWJsZTs%2BO2w86Iez5LuK5pyq6YCa6L%2BH6K%2B%2B56iL5oiQ57up77yaO288dD47Pj47Pjs7Pjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjtwPGw8c3R5bGU7PjtsPERJU1BMQVk6YmxvY2s7Pj4%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjtsPGNrX2R5Oz4%2BXZUjpstynTgGKTsJaNPNUY2GmeU%3D");
        params.put("__VIEWSTATEGENERATOR", "9727EB43");
        params.put("ddlXN", "");
        params.put("ddlXQ", "");
        params.put("ddl_kcxz", "");
        params.put("btn_xq", "%B3%C9%BC%A8%CD%B3%BC%C6");
        params.put(COOKIE, EduInfo.getCookie());
        params.put(REFERER, url);


        MyHttpCallBack callBack = new MyHttpCallBack() {
            @Override
            public void onSuccess(Map<String, String> headers, int respCode, String response) {
                Logger.d("成绩统计查询成功:" + respCode + " " + headers.get(CONTENT_LENGTH) + " ");
//                EduGrade.saveGradeHtml(response);
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_QUERY_GRADE_SUCCESS));
            }

            @Override
            public void onFailure(Map<String, String> headers, int respCode) {
                Logger.d("成绩统计查询失败");
                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_QUERY_GRADE_FAIL));
            }
        };
        post(url, params, callBack);
    }


    //-------------------------------------------------------------------------------------


    /**
     * 发送Http请求 进入教务系统主页 获取cookie
     *
     */
//    public static void login(final String username,final String password,final int loginTag) {
//        final String address = CSMY_EDUCATION_HOME;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    //成功
//                    if (connection.getResponseCode() == 200) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "GBK")
//                        );
//                        StringBuffer response = new StringBuffer();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line + "\n");
//                        }
//                        String cookie = connection.getHeaderField("Set-Cookie");
//                        //保存cookie
//                        LoginUtil.saveCookie(cookie);
////                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_HOME_SUCCESS,
////                                cookie));
//
//                        Log.i(TAG, "enter education home success");
//                        loginEducation(username,password,loginTag);
//                    } else {
//                        Log.i(TAG, "enter education home fail");
//                        EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_LOGIN_FAIL,EDUCATION_LOGIN_ERROR));
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if(in != null)
//                            in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//    }

//    /**
//     * 发送Http请求 进入教务系统内部
//     *
//     * @param username 学号
//     */
//    public static void enterEducationInterior(String username,final int loginTag) {
//        final String address = CSMY_BASE_URL_XH + username;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    //设置cookie
//                    connection.setRequestProperty(COOKIE, LoginUtil.getCookie());
//                    //成功
//                    if (connection.getResponseCode() == 200) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "GBK")
//                        );
//                        StringBuffer response = new StringBuffer();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line + "\n");
//                        }
//                        Log.i(TAG, "enter education interior success");
//                        if(loginTag == EventTag.SWITCH_SCHEDULE){
//                            EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_OBTAIN_SCHEDULE));
//                        }else{
//                            EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_LOGIN_SUCCESS,
//                                    loginTag));
//                        }
//                    } else {
//                        Log.i(TAG, "enter education interior fail");
//                        EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_LOGIN_FAIL,EDUCATION_LOGIN_ERROR));
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (in != null) in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//
//    }


//    /**
//     * 发送http post 请求 登录教务系统
//     *
//     * @param username 学号
//     * @param password 密码
//     */
//    public static void loginEducation(final String username, final String password,final int loginTag) {
//        final String address = CSMY_EDUCATION_HOME;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                OutputStream out = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty(COOKIE, LoginUtil.getCookie());
//                    //传递的数据
//                    String params = "__VIEWSTATE=dDw3OTkxMjIwNTU7Oz4BO%2FY3htAyQJF%2FNwKj5qZqDUYy9g%3D%3D" +
//                            "&__VIEWSTATEGENERATOR=92719903"
//                            + "&TextBox1=" + username
//                            + "&TextBox2=" + password
//                            + "&TextBox3="
//                            + "&RadioButtonList1=%D1%A7%C9%FA"
//                            + "&Button1=";
//
//                    //获取输出流
//                    out = connection.getOutputStream();
//                    out.write(params.getBytes());
//                    out.flush();
//
//                    int responseCode = connection.getResponseCode();
//                    int contentLength = connection.getContentLength();
//                    String msg = connection.getResponseMessage();
//
//                    if (contentLength > 5000) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "gb2312")
//                        );
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        Log.i(TAG, "login education success");
//                        enterEducationInterior(username,loginTag);
////                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_LOGIN_SUCCESS,
////                                responseCode+" content length:"+contentLength+
////                                    " msg:"+msg));
//                    } else {
//                        Log.i(TAG, "login education fail:responseCode="+responseCode+"contentLength="+contentLength);
//                        EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_LOGIN_FAIL,EDUCATION_PASSWORD_ERROR));
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (in != null)
//                            in.close();
//                        if (out != null)
//                            out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//    }

//    /**
//     * 发送http post 请求 获取教务系统课表数据
//     */
//    public static void obtainSchedule(final String academicYear, final String term) {
//        final String username = EduInfo.getEducationUserName();
//        final String address = CSMY_BASE_URL_SCHEDULE_PART_ONE + username +CSMY_BASE_URL_SCHEDULE_PART_TWO;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                OutputStream out = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty(COOKIE, EduInfo.getCookie());
//
//                    //设置Referer
//                    String referer = address;
//                    connection.setRequestProperty(REFERER, referer);
//                    Log.i("MainActivity", "run: " + connection.getRequestMethod());
//                    //传递的数据
//
//                    String params = "__EVENTTARGET=xqd"+
//                            "&__EVENTARGUMENT=" +
//                            "&__VIEWSTATE=dDwtODAxODI2NDQzO3Q8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw5Pjs%2BO2w8dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDM%2BO2k8Nj47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w8XGU7Pj47Pjs7Pjt0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8eG47eG47Pj47Pjt0PGk8ND47QDwyMDE2LTIwMTc7MjAxNS0yMDE2OzIwMTQtMjAxNTtcZTs%2BO0A8MjAxNi0yMDE3OzIwMTUtMjAxNjsyMDE0LTIwMTU7XGU7Pj47bDxpPDE%2BOz4%2BOzs%2BO3Q8dDw7O2w8aTwxPjs%2BPjs7Pjt0PDtsPGk8MD47PjtsPHQ8dDxwPHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOz47O2w8aTwwPjs%2BPjs7Pjs%2BPjs%2BPjs%2BPjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwxPjtpPDM%2BO2k8NT47aTw3PjtpPDk%2BOz47bDx0PHQ8cDxwPGw8RGF0YVRleHRGaWVsZDtEYXRhVmFsdWVGaWVsZDs%2BO2w8bmo7bmo7Pj47Pjt0PGk8MTU%2BO0A8MjAxNjsyMDE1OzIwMTQ7MjAxMzsyMDEyOzIwMTE7MjAxMDsyMDA5OzIwMDg7MjAwNzsyMDA2OzIwMDU7MjAwNDsyMDAzO1xlOz47QDwyMDE2OzIwMTU7MjAxNDsyMDEzOzIwMTI7MjAxMTsyMDEwOzIwMDk7MjAwODsyMDA3OzIwMDY7MjAwNTsyMDA0OzIwMDM7XGU7Pj47bDxpPDA%2BOz4%2BOzs%2BO3Q8dDxwPHA8bDxEYXRhVGV4dEZpZWxkO0RhdGFWYWx1ZUZpZWxkOz47bDx4eW1jO3h5ZG07Pj47Pjt0PGk8MTE%2BO0A85ZWG5a2m6ZmiO%2BeUteWtkOS%2FoeaBr%2BW3peeoi%2BWtpumZojvnpL7kvJrnrqHnkIblrabpmaI756S%2B5Lya5bel5L2c5a2m6ZmiO%2BauoeS7quWtpumZojvoibrmnK%2FlrabpmaI75paH5YyW5Lyg5pKt5a2m6ZmiO%2Bi9r%2BS7tuWtpumZojvljLvlrabpmaI75aSW6K%2Bt5a2m6ZmiO1xlOz47QDwxODsxOTsyMDsyMTsyMjsyMzsyNDsyNTsyNjsyNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHp5bWM7enlkbTs%2BPjs%2BO3Q8aTw4PjtAPOeUteWtkOWVhuWKoTvlm73pmYXnu4%2FmtY7kuI7otLjmmJM75Lya6K6hO%2BW4guWcuuiQpemUgDvnianmtYHnrqHnkIY76K%2BB5Yi45oqV6LWE5LiO566h55CGO%2BivgeWIuOS4juacn%2Bi0pztcZTs%2BO0A8MTgwNTsxODAzOzE4MDE7MTgwMjsxODA2OzE4MDQ7MTgwNztcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPGJqbWM7YmpkbTs%2BPjs%2BO3Q8aTw0PjtAPOeUteWVhjE2MzM755S15ZWGMTYzMTvnlLXllYYxNjMyO1xlOz47QDwxNjE4MDUzMzsxNjE4MDUzMTsxNjE4MDUzMjtcZTs%2BPjtsPGk8MD47Pj47Oz47dDx0PHA8cDxsPERhdGFUZXh0RmllbGQ7RGF0YVZhbHVlRmllbGQ7PjtsPHhtO3hoOz4%2BOz47dDxpPDE%2BO0A8XGU7PjtAPFxlOz4%2BOz47Oz47Pj47Pj47dDw7bDxpPDA%2BOz47bDx0PDtsPGk8MD47aTwyPjtpPDQ%2BO2k8Nj47aTw4PjtpPDExPjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDzlrablj7fvvJoxNDI1MTEzMjEyOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzlp5PlkI3vvJrpg63ms73mtps7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOWtpumZou%2B8mui9r%2BS7tuWtpumZojs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia77ya6L2v5Lu25oqA5pyv77yI56e75Yqo5bqU55So5byA5Y%2BR5pa55ZCR77yJOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzooYzmlL%2Fnj63vvJrnp7vliqgxNDMyOz4%2BOz47Oz47dDx0PDs7bDxpPDA%2BOz4%2BOzs%2BOz4%2BOz4%2BO3Q8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwxPjtpPDE%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47bDxpPDA%2BOz47bDx0PDtsPGk8MT47PjtsPHQ8O2w8aTwwPjtpPDE%2BO2k8Mj47aTwzPjtpPDQ%2BO2k8NT47PjtsPHQ8cDxwPGw8VGV4dDs%2BO2w85LiT5Lia5rex5bqm56S%2B5Lya5a6e6Le1Oz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDzliJjljZPlpoI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDEuMDs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w8MDItMTk7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPCZuYnNwXDs7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjt0PHA8bDxWaXNpYmxlOz47bDxvPGY%2BOz4%2BOzs%2BO3Q8cDxsPFZpc2libGU7PjtsPG88Zj47Pj47bDxpPDA%2BOz47bDx0PDtsPGk8MD47PjtsPHQ8QDA8cDxwPGw8UGFnZUNvdW50O18hSXRlbUNvdW50O18hRGF0YVNvdXJjZUl0ZW1Db3VudDtEYXRhS2V5czs%2BO2w8aTwxPjtpPDA%2BO2k8MD47bDw%2BOz4%2BOz47Ozs7Ozs7Ozs7Pjs7Pjs%2BPjs%2BPjt0PEAwPHA8cDxsPFBhZ2VDb3VudDtfIUl0ZW1Db3VudDtfIURhdGFTb3VyY2VJdGVtQ291bnQ7RGF0YUtleXM7PjtsPGk8MT47aTwwPjtpPDA%2BO2w8Pjs%2BPjs%2BOzs7Ozs7Ozs7Oz47Oz47dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDE%2BO2k8MT47aTwxPjtsPD47Pj47Pjs7Ozs7Ozs7Ozs%2BO2w8aTwwPjs%2BO2w8dDw7bDxpPDE%2BOz47bDx0PDtsPGk8MD47aTwxPjtpPDI%2BO2k8Mz47aTw0Pjs%2BO2w8dDxwPHA8bDxUZXh0Oz47bDwyMDE1LTIwMTY7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPDI7Pj47Pjs7Pjt0PHA8cDxsPFRleHQ7PjtsPOS4k%2BS4mua3seW6puekvuS8muWunui3tTs%2BPjs%2BOzs%2BO3Q8cDxwPGw8VGV4dDs%2BO2w85YiY5Y2T5aaCOz4%2BOz47Oz47dDxwPHA8bDxUZXh0Oz47bDwxLjA7Pj47Pjs7Pjs%2BPjs%2BPjs%2BPjs%2BPjs%2B8U%2FrZesVxymifx8EKT7RuFOUgVc%3D"+
//                            "&__VIEWSTATEGENERATOR=55530A43"+
//                            "&xnd="+academicYear+
//                            "&xqd="+term;
//
//                        //获取输出流
//                        out = connection.getOutputStream();
//                        out.write(params.getBytes());
//                        out.flush();
//
//
//                    int responseCode = connection.getResponseCode();
//                    Log.i(TAG, "obtain schedule:"+responseCode);
//
//
//                    if (responseCode <= 302) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "gb2312")
//                        );
//
//
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//
//                        Logger.d("获取课表成功："+response.toString());
//
//                        if(response.toString().contains(context.getString(R.string.need_common))){
//                            EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_SWITCH_SCHEDULE_FAIL, context.getString(R.string.need_common)));
//                            return;
//                        }
//                        EduSchedule.saveScheduleHtml(response.toString());
//                        List<Course> courseList = EduSchedule.getEducationScheduleCourses(false);
//                        DataSupport.saveAll(courseList);
//                        EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS, EventTag.SWITCH_SCHEDULE));
//                    } else {
//                        Logger.d("获取课表失败："+ responseCode);
//
//                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_SWITCH_SCHEDULE_FAIL, context.getString(R.string.try_again_later)));
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (in != null)
//                            in.close();
//                        if (out != null)
//                            out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//    }

//    /**
//     * 发送http post 请求 获取个人信息
//     * @param username             学号
//     */
//    public static void obtainPersonalInfo(final String username) {
//        final String address = CSMY_BASE_URL_PERSONAL_INFO_PART_ONE + username +CSMY_BASE_URL_PERSONAL_INFO_PART_TWO;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                OutputStream out = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty(COOKIE, LoginUtil.getCookie());
//                    //设置Referer
//
//                    String referer = CSMY_REFERER_URL+username;
//                    connection.setRequestProperty(REFERER, referer);
//                    Log.i("MainActivity", "run: " + connection.getRequestMethod());
//
//
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode <= 302) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "gb2312")
//                        );
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS, response.toString()));
//                    } else {
//                        EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL, responseCode + ""));
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (in != null)
//                            in.close();
//                        if (out != null)
//                            out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//    }


//    /**
//     * 获取教务系统信息
//     * @param username 学号
//     * @param tag 获取信息的类型
//     */
//    public static void obtainEducationInfo(final String username,final int tag) {
//        String urlA = null;
//        String urlB = null;
//        switch (tag){
//            case EDUCATION_SCHEDULE:
//                urlA = CSMY_BASE_URL_SCHEDULE_PART_ONE;
//                urlB = CSMY_BASE_URL_SCHEDULE_PART_TWO;
//                break;
//            case EDUCATION_PERSONAL_INFO:
//                urlA = CSMY_BASE_URL_PERSONAL_INFO_PART_ONE;
//                urlB = CSMY_BASE_URL_PERSONAL_INFO_PART_TWO;
//                break;
//            case EDUCATION_GRADE:
//                urlA = CSMY_BASE_URL_GRADE_HOME_PART_ONE;
//                urlB = CSMY_BASE_URL_GRADE_HOME_PART_TWO;
//                break;
//        }
//        final String address = urlA + username +urlB;
//        final boolean[] isSuccessed = {false};
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                InputStream in = null;
//                OutputStream out = null;
//                try {
//                    URL url = new URL(address);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(20000);
//                    connection.setReadTimeout(20000);
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
//                    connection.setRequestProperty(COOKIE, LoginUtil.getCookie());
//                    //设置Referer
//
//                    String referer = CSMY_REFERER_URL+username;
//                    connection.setRequestProperty(REFERER, referer);
//                    Log.i("MainActivity", "run: " + connection.getRequestMethod());
//
//                    String responseStr = null;
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode <= 302) {
//                        in = connection.getInputStream();
//                        BufferedReader reader = new BufferedReader(
//                                new InputStreamReader(in, "gb2312")
//                        );
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        responseStr = response.toString();
//                        isSuccessed[0] = true;
//                    }
//
//                    switch (tag){
//                        case EDUCATION_SCHEDULE:
//                            if(isSuccessed[0]){
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS, responseStr));
//                                Log.i(TAG, "obtain schedule success");
//                            }else{
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_SCHEDULE_FAIL, responseCode + ""));
//                                Log.i(TAG, "obtain schedule fail");
//                            }
//                            break;
//                        case EDUCATION_PERSONAL_INFO:
//                            if(isSuccessed[0]){
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS, responseStr));
//                                Log.i(TAG, "obtain personal info success");
//                            }else{
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL, responseCode + ""));
//                                Log.i(TAG, "obtain personal info fail");
//                            }
//                            break;
//                        case EDUCATION_GRADE:
//                            if(isSuccessed[0]){
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_GRADE_HOME_SUCCESS, responseStr));
//                            }else{
//                                EventBus.getDefault().post(new EventModel<String>(Event.EDUCATION_GRADE_HOME_FAIL, responseCode + ""));
//                            }
//                            break;
//                    }
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (in != null)
//                            in.close();
//                        if (out != null)
//                            out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//
//
//    }

}
