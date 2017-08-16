package com.xdja.cache.okhttp.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import com.xdja.cache.okhttp.request.CacheType;
import com.xdja.cache.okhttp.request.OkHttpCacheUtils;
import com.xdja.cache.okhttp.strategy.CacheNetworkStrategy;
import com.xdja.cache.okhttp.strategy.CacheStrategy;
import com.xdja.cache.okhttp.strategy.NetworkCacheStrategy;
import com.xdja.cache.okhttp.strategy.NetworkStrategy;
import com.xdja.cache.okhttp.strategy.RequestStrategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 缓存数据拦截器
 *
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        RequestStrategy requestStrategy = new RequestStrategy();
        String cacheTypeHeader = chain.request().headers().get(OkHttpCacheUtils.REQUEST_CACHE_TYPE_HEAD);
        if (cacheTypeHeader != null) {
            int cacheType = Integer.parseInt(cacheTypeHeader);
            Log.i("111", "请求tag:" + cacheType + " 请求url:" + chain.request().url().toString());
            switch (cacheType) {
                case CacheType.ONLY_CACHE:
                    requestStrategy.setBaseRequestStrategy(new CacheStrategy());
                    break;
                case CacheType.ONLY_NETWORK:
                    requestStrategy.setBaseRequestStrategy(new NetworkStrategy());
                    break;
                case CacheType.CACHE_ELSE_NETWORK:
                    requestStrategy.setBaseRequestStrategy(new CacheNetworkStrategy());
                    break;
                case CacheType.NETWORK_ELSE_CACHE:
                    requestStrategy.setBaseRequestStrategy(new NetworkCacheStrategy());
                    break;
                case CacheType.INVALID_TYPE:
                    break;
                default:
                    break;
            }
        }
        return requestStrategy.request(chain);
    }
}
