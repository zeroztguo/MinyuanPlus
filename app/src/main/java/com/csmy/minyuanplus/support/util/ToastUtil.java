package com.csmy.minyuanplus.support.util;

import android.widget.Toast;

/**
 * Created by Zero on 16/6/24.
 */
public class ToastUtil extends Util{
    public static void show(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

}
