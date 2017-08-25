package com.xdja.cache.common.interceptor;

import android.util.Log;

import com.xdja.cache.common.strategy.CacheNetworkStrategy;
import com.xdja.cache.common.strategy.OnlyCacheStrategy;
import com.xdja.cache.common.strategy.NetworkCacheStrategy;
import com.xdja.cache.common.strategy.OnlyNetworkStrategyOk;
import com.xdja.cache.common.strategy.RequestStrategy;
import com.xdja.cache.common.utils.Common;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 缓存数据拦截器
 * <p>Author:yusenkui</p>
 * <p>Date:2017/8/14</p>
 */
public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        RequestStrategy requestStrategy = new RequestStrategy();
        String cacheTypeHeader = chain.request().headers().get(Common.REQUEST_CACHE_TYPE_HEAD);
        if (cacheTypeHeader != null) {
            int cacheType = Integer.parseInt(cacheTypeHeader);
            Log.i("111", "请求tag:" + cacheType + " 请求url:" + chain.request().url().toString());
            switch (cacheType) {
                case CacheType.ONLY_CACHE:
                    requestStrategy.setBaseRequestStrategy(new OnlyCacheStrategy());
                    break;
                case CacheType.ONLY_NETWORK:
                    requestStrategy.setBaseRequestStrategy(new OnlyNetworkStrategyOk());
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
