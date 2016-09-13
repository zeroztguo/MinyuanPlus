package com.csmy.minyuanplus.support.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.litepal.LitePalApplication;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 民院+工具类
 * Created by Zero on 16/6/24.
 */
public class Util {

    protected static Context context = LitePalApplication.getContext();
    private static int dayOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//    //是否第一次进入app的常量
//    private static final String IS_FIRST_ENTER_APP = "isFirstEnterApp";
    //当前版本号
    private static final String CURRENT_VERSION_CODE = "current_version_code";


    /**
     * @return 格式化后的日期
     */
    public static int formatDayOfYear(int year, int month, int day) {
        int fDay = 0;
        for (int i = 0; i < month - 1; i++) {
            if (i == 1) {
                if (isLeapYear(year)) {
                    fDay += 29;
                } else {
                    fDay += dayOfMonth[i];
                }
            }
            fDay += dayOfMonth[i];
        }
        fDay += day;

        return fDay;
    }

//    /**
//     * 设置是否第一次进入应用
//     *
//     * @param isFirstEnterApp
//     */
//    public static void setFirstEnterApp(boolean isFirstEnterApp) {
//        SPUtil.put(IS_FIRST_ENTER_APP, IS_FIRST_ENTER_APP);
//    }
//
//    /**
//     * @return 是否第一次进入应用
//     */
//    public static boolean getFirstEnterApp() {
//        return (boolean) SPUtil.get(IS_FIRST_ENTER_APP, true);
//    }

    /**
     * 设置当前版本号
     * @param versionCode
     */
    public static void setCurrentVersionCode(int versionCode) {
        SPUtil.put(CURRENT_VERSION_CODE, versionCode);
    }

    /**
     *
     * @return 当前版本号
     */
    public static int getCurrentVersionCode() {
        return (int) SPUtil.get(CURRENT_VERSION_CODE, 0);
    }

    /**
     * @return 本周周一的格式化后天数
     */
    public static int curretMondayOfWeekToDayOfYear(int fDay, int week) {
        int cmdDay = fDay - (week - 1);
        return cmdDay;
    }


    /**
     * 判断当前日期是星期几
     *
     * @return 判断结果
     */
    public static int dayForWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String pTime = format.format(date);

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek = 0;

        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }


    /**
     * 是否是闰年
     *
     * @param year 年份
     * @return 是否
     */
    public static boolean isLeapYear(int year) {
        boolean isLeapYear = false;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            isLeapYear = true;
        }
        return isLeapYear;
    }



    /**
     * 获取网络类型
     *
     * @return
     */
    public static int getNetWorkType() {
        int networkType = NetworkType.NETWORKTYPE_INVAILD;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                networkType = NetworkType.NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                networkType = NetworkType.NETWORKTYPE_MOBILE;
            } else {
                networkType = NetworkType.NETWORKTYPE_INVAILD;
            }
        }
        return networkType;
    }

    public static boolean isStringNull(String string) {
        if (null == string || "".equals(string)) {
            return true;
        }
        return false;
    }

    public static boolean isArrayNull(Object[] array) {
        if (null == array || array.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * @return 应用版本码
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * @return 应用版本号
     */
    public static String getVersionName() {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName()
                    , PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Bitmap转字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bm != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        return baos.toByteArray();
    }

    /**
     * 字节数组转Bitmap
     *
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b != null && b.length >0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 字符数字转整形
     *
     * @return
     */
    public static int numberString2int(String s) {
        if (s.equals("一")) {
            return 1;
        } else if (s.equals("二")) {
            return 2;
        } else if (s.equals("三")) {
            return 3;
        } else if (s.equals("四")) {
            return 4;
        } else if (s.equals("五")) {
            return 5;
        } else if (s.equals("六")) {
            return 6;
        } else if (s.equals("七")) {
            return 7;
        } else if (s.equals("八")) {
            return 8;
        } else if (s.equals("九")) {
            return 9;
        }
        return 1;
    }


    // MD5加密，32位
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    // 可逆的加密算法
    public static String encryptmd5(String str) {
        char[] a = str.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 'l');
        }
        String s = new String(a);
        return s;
    }


    /**
     * 格式化从Calendar中获取的星期数
     *
     * @param calendarWeek Calendar中获取的星期数
     * @return
     */
    public static int getFormatWeekNum(int calendarWeek) {
        int week = calendarWeek - 1;
        if (week == 0) {
            week = 7;
        }
        return week;
    }


    /**
     * @return 当天的日期，格式如：2016-9-5
     */
    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-"
                + cal.get(Calendar.MONTH) + "-"
                + cal.get(Calendar.DAY_OF_MONTH);
    }

}
