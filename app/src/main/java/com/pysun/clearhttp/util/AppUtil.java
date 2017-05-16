package com.pysun.clearhttp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.pysun.clearhttp.App;


/**
 * Created by Administrator on 2017/3/30.
 */

public class AppUtil {
    private final static String SHARED_NAME = "pysun";
    private static App mApp;

    public static void init(App app) {

        mApp = app;
    }

    public static SharedPreferences getSharedPreferences() {
        return mApp.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }
}
