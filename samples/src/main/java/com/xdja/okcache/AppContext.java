package com.xdja.okcache;

import android.app.Application;

import com.xdja.okcache.okhttp.request.OkHttpCacheUtils;
import com.xdja.okcache.retrofit.generator.RetrofitCacheGenerator;

/**
 * Created by Administrator on 2016/10/17.
 */
public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpCacheUtils.init(this);
        RetrofitCacheGenerator.init(this);
    }
}
