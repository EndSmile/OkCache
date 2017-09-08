package com.xdja.okcache.sample.retrofit;


import android.content.Context;

import com.xdja.okcache.interceptor.OkCacheInterceptor;
import com.xdja.okcache.retrofit.adapter.rxjava.RxJavaCallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>Summary:retrofit初始化</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/29</p>
 * <p>Time:14:59</p>
 */

public class RetrofitGenerator {
    private Retrofit.Builder builder;
    private static OkHttpClient client = null;

    public static final String HOST = "https://api.github.com";
    private Context context;

    public RetrofitGenerator(Context context) {
        this.context = context;
        initOkHttp();
        initRetrofit();
    }


    private void initRetrofit() {
        builder = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //注意顺序，日志拦截器在前则可以看见缓存请求，否则看不到缓存请求
        builder.addInterceptor(new OkCacheInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        builder.connectTimeout(15, TimeUnit.SECONDS)   //设置超时
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);   //错误重连 关于这个错误有三种
        // 1.无法访问ip地址 2.过时的连接池连接 3.无法连接代理服务器
        client = builder.build();
    }


}
