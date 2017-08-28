package com.xdja.okcache.common.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.xdja.okcache.common.constant.CacheStrategy;
import com.xdja.okcache.common.strategy.CacheNetworkStrategy;
import com.xdja.okcache.common.strategy.NetworkCacheStrategy;
import com.xdja.okcache.common.strategy.custom.OnlyNetworkStrategy;
import com.xdja.okcache.common.strategy.RequestStrategy;
import com.xdja.okcache.common.utils.OkCacheParamsKey;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
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
        Request request = chain.request();
        String cacheTypeHeader = request.headers().get(OkCacheParamsKey.CACHE_STRATEGY_HEADER);
        String query = request.url().url().getQuery();
        int cacheTime = getCacheTime(query);
        if (cacheTypeHeader != null) {
            int cacheType = Integer.parseInt(cacheTypeHeader);
            Log.i("111", "请求tag:" + cacheType + " 请求url:" + request.url().toString());
            switch (cacheType) {
                case CacheStrategy.ONLY_CACHE:
                    requestStrategy.setBaseRequestStrategy(new com.xdja.okcache.common.strategy.CacheStrategy(cacheTime));
                    break;
                case CacheStrategy.ONLY_NETWORK:
                    requestStrategy.setBaseRequestStrategy(new OnlyNetworkStrategy());
                    break;
                case CacheStrategy.CACHE_ELSE_NETWORK:
                    requestStrategy.setBaseRequestStrategy(new CacheNetworkStrategy(cacheTime));
                    break;
                case CacheStrategy.NETWORK_ELSE_CACHE:
                    requestStrategy.setBaseRequestStrategy(new NetworkCacheStrategy(cacheTime));
                    break;
                case CacheStrategy.INVALID_TYPE:
                    break;
                default:
                    break;
            }
        }
        return requestStrategy.request(chain);
    }

    private int getCacheTime(String query) {
        if (!TextUtils.isEmpty(query)) {
            if (query.contains("&")) {
                String[] split = query.split("&");
                for (String s : split) {
                    if ("cacheTime".equals(s.split("=")[0])) {
                        return Integer.parseInt(query.split("=")[1]);
                    }
                }
            } else {
                if ("cacheTime".equals(query.split("=")[0])) {
                   return Integer.parseInt(query.split("=")[1]);
                }
            }
        }
        return 0;
    }
}
