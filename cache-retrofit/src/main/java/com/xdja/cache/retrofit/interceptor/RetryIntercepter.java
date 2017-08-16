package com.xdja.cache.retrofit.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>Summary:失败重试拦截器</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample.interceptor</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/31</p>
 * <p>Time:15:01</p>
 */


public class RetryIntercepter implements Interceptor {

    public int maxRetry;//最大重试次数
    private static String TAG = "RetryIntercepter";

    public RetryIntercepter(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        //打印传出请求相关信息
        Log.i("RetryIntercepter", String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);
        //打印传入相应相关信息
        long t2 = System.nanoTime();
        Log.i("RetryIntercepter", String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        int tryCount = 0;
        //失败的最大次数
        while (!response.isSuccessful() && tryCount < maxRetry) {
            tryCount++;
            //重新获取相应结果
            response = chain.proceed(request);
        }
        return response;

    }
}
