package com.pysun.clearhttp;

import android.app.Application;

import com.pysun.clearhttp.util.AppUtil;


/**
 * Created by Administrator on 2017/3/10.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.init(this);
    }

}
