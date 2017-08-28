package com.xdja.okcache.retrofit.generator;


import android.content.Context;

import com.xdja.okcache.retrofit.util.SdUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>Summary:retrofit初始化</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/29</p>
 * <p>Time:14:59</p>
 */


public class RetrofitCacheGenerator {
    private Retrofit.Builder builder;
    private static OkHttpClient client = null;
    private static Context mContext;

    public static final String HOST = "https://api.github.com";

    public RetrofitCacheGenerator() {
        initOkHttp();
        initRetrofit();
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private void initRetrofit() {
        builder = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        File cacheFile = new File(SdUtils.getDiskCacheDir(mContext));
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.cache(cache)
                .addInterceptor(loggingInterceptor);
        //// TODO: 2017/8/28 待修复
//                .addInterceptor(new CacheInterceptor());
        builder.connectTimeout(15, TimeUnit.SECONDS)   //设置超时
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);   //错误重连 关于这个错误有三种
        // 1.无法访问ip地址 2.过时的连接池连接 3.无法连接代理服务器
        client = builder.build();
    }


}
