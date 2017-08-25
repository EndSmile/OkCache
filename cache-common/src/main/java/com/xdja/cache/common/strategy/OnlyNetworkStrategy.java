package com.xdja.cache.common.strategy;


import com.xdja.cache.common.OkCache;
import com.xdja.cache.common.utils.Common;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkCacheOperation;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 仅仅请求网络策略
 */
public class OnlyNetworkStrategy implements IOkCacheStrategy {
    private static final float MAX_AGE = 0;
    private float mMaxAge; //表示当访问此网页后的max-age秒内再次访问不会去服务器请求

    public OnlyNetworkStrategy() {
        mMaxAge = MAX_AGE;
    }

    public OnlyNetworkStrategy(float maxAge) {
        this.mMaxAge = MAX_AGE;
    }

    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
//        Request request = chain.request();
//        request = request.newBuilder()
//                .addHeader("Cache-Control", "public, max-age=" + mMaxAge)
//                .removeHeader(Common.REQUEST_CACHE_TYPE_HEAD)//移除添加的自定义header
//                .build();
//        Response response = chain.proceed(request);
//        response = response.newBuilder()
//                .removeHeader("Control")
//                .addHeader("Cache-Control", "public, max-age=" + mMaxAge)
//                .removeHeader("Pragma")
//                .build();

        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isSuccessful()){
            OkCacheOperation cacheOperation = OkCache.getCacheOperation();
            cacheOperation.put(response);
            OkCache.putCacheTime(OkCacheOperation.key(request.url()));
        }

        return response;
    }
}
