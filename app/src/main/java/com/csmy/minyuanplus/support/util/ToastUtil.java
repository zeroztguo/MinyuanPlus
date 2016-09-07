package com.csmy.minyuanplus.support.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by Zero on 16/6/24.
 */
public class ToastUtil {
    public static void showLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
