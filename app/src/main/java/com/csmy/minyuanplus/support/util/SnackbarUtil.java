package com.csmy.minyuanplus.support.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Zero on 16/6/23.
 */
public class SnackbarUtil {

    public static void showWithNoAction(View view,String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }
}
