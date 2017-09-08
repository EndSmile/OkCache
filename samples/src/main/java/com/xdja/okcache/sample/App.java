package com.xdja.okcache.sample;

import android.app.Application;

import com.xdja.okcache.OkCache;

/**
 * Created by ldy on 2016/9/8.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkCache.init(this,new OkCache.InitParams().enableGetCache());
    }
}
