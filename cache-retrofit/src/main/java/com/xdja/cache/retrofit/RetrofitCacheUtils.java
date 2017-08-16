package com.xdja.cache.retrofit;


import com.xdja.cache.common.utils.Common;
import com.xdja.cache.retrofit.bean.Contributor;
import com.xdja.cache.retrofit.interceptor.HaveQueryInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
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


public class RetrofitCacheUtils {
    private static ApiInterface sApiService = null;
    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;

    public RetrofitCacheUtils() {
        initOkHttp();
        initRetrofit();
        sApiService = retrofit.create(ApiInterface.class);
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Common.HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static RetrofitCacheUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 静态内部类实现单例
     * 加载HttpSingleton 类时不会初始化instance 只有在调用getInstance 方法时，才会导致instance 被初始化,
     * 这个方法不仅能够确保线程安全，也能够保证 单例对象的唯一性,同时也延迟了单例的实例化
     */
    private static class SingletonHolder {
        private static final RetrofitCacheUtils INSTANCE = new RetrofitCacheUtils();
    }


    private void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        File cacheFile = new File(Common.NET_CACHE);
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.cache(cache)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new HaveQueryInterceptor());
        builder.connectTimeout(15, TimeUnit.SECONDS)   //设置超时
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);   //错误重连 关于这个错误有三种
        // 1.无法访问ip地址 2.过时的连接池连接 3.无法连接代理服务器
        client = builder.build();
    }

    /**
     * @param isUseCache 是否使用缓存
     * @param cacheTime  缓存时间 秒
     * @return
     */
    public List<Contributor> fetchContributor(boolean isUseCache, int cacheTime) {
        try {
            Call<List<Contributor>> contributors = sApiService.contributors("square", "retrofit", cacheTime, isUseCache);
            Response<List<Contributor>> execute = contributors.execute();
            return execute.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
