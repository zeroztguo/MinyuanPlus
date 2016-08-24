package com.csmy.minyuanplus.support.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.csmy.minyuanplus.application.App;

import org.litepal.LitePalApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zero on 16/6/24.
 */
public class Util {

    protected static Context context = LitePalApplication.getContext();
    private static int dayOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    protected static App mApp = (App) context;

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
     * 保存信息到本地
     */
    public static void write(String path, String info) {
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = info.getBytes();
            fos.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static int getNetWorkType() {
        int networkType = NetworkType.NETWORKTYPE_INVAILD;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public static int getVersionCode(){
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * @return 应用版本号
     */
    public static String getVersionName(){
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


}
