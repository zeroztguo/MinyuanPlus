package com.csmy.minyuanplus.support.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Snackbar的工具类
 * Created by Zero on 16/6/23.
 */
public class SnackbarUtil {
    public static void showSnackShort(View view,String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
    }

    public static void showSnackLong(View view,String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }
}
