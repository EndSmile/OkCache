package com.xdja.cache.common.strategy;

import com.xdja.cache.common.OkCache;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * 仅仅请求缓存策略
 */
public class OnlyCacheStrategy implements IOkCacheStrategy {

    private static final float MAX_STALE = 60 * 60 * 24 * 30;//过期时间为30天
    private float mMaxStale;//缓存过期时间

    public OnlyCacheStrategy() {
        mMaxStale = MAX_STALE;
    }

    public OnlyCacheStrategy(float maxStale) {
        this.mMaxStale = maxStale;
    }

    /**
     * 请求策略
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
//        Request request = chain.request();
//        request = request.newBuilder()
//                .cacheControl(CacheControl.FORCE_CACHE)
//                .removeHeader(Common.REQUEST_CACHE_TYPE_HEAD)//移除添加的自定义header
//                .build();//没有网络，直接读取缓存
//        Response response = chain.proceed(request);
//        response = response.newBuilder()// only-if-cached完全使用缓存，如果命中失败，则返回503错误
//                .header("Cache-Control", "public,max-stale=" + mMaxStale)
//                .removeHeader("Pragma")
//                .build();

        Request request = chain.request();
        Response response = OkCache.getCacheOperation().get(request);
        if (response==null){
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(504)
                    .message("Unsatisfiable Request (only-if-cached)")
                    .body(Util.EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }
        return response;
    }
}
