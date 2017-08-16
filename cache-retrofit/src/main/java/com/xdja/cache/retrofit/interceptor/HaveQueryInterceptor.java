package com.xdja.cache.retrofit.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xdja.cache.retrofit.bean.CacheBean;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <p>Summary:缓存拦截器</p>
 * <p>Description:</p>
 * <p>Package:com.xdja.retrofitsample.interceptor</p>
 * <p>Author:yusenkui</p>
 * <p>Date:2017/7/31</p>
 * <p>Time:15:07</p>
 */


public class HaveQueryInterceptor implements Interceptor {


    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        URL url = request.url().url();
        String query = url.getQuery();//url上拼上的请求参数
        CacheBean cacheMsg = getCacheMsg(query);
        if (cacheMsg != null) {
            if (cacheMsg.isUseCache()) {
                request = request.newBuilder()
                        .cacheControl(new CacheControl.Builder().maxAge(cacheMsg.getCacheTime(), TimeUnit.SECONDS).build())
                        .build();
            }
        }
        Response response = chain.proceed(request);
        Response.Builder newBuilder = response.newBuilder();
        return newBuilder.build();
    }

    private CacheBean getCacheMsg(String query) {
        if (!TextUtils.isEmpty(query)) {
            CacheBean cacheBean = new CacheBean();
            String[] split = query.split("&");
            String cacheTime = split[0].split("=")[1];
            String isCache = split[1].split("=")[1];
            cacheBean.setCacheTime(Integer.parseInt(cacheTime));
            cacheBean.setUseCache(isCache.equals("true"));
            return cacheBean;
        }
        return null;
    }
}
