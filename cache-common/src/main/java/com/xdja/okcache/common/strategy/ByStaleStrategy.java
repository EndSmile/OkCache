package com.xdja.okcache.common.strategy;

import com.xdja.okcache.common.OkCache;
import com.xdja.okcache.common.constant.QueryParams;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkCacheOperation;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ldy on 2017/8/25.
 *
 * 如果有缓存且缓存未过期则使用缓存，否则使用网络
 */

public class ByStaleStrategy implements IRequestStrategy {
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {

        Request request = chain.request();
        String maxStaleStr = request.url().queryParameter(QueryParams.MAX_STALE);

        //获取最大过期时间
        long maxStale;
        try {
            maxStale = Long.valueOf(maxStaleStr);
        } catch (Exception e) {
            maxStale = OkCache.getMaxStale();
        }

        Response cacheResponse = new OnlyCacheStrategy().request(chain);
        if (cacheResponse.isSuccessful()) {
            long cacheTime = OkCache.getCacheTime(OkCacheOperation.getKey(request));
            if (System.currentTimeMillis() - cacheTime <= maxStale) {
                //没有过期则使用缓存
                return cacheResponse;
            }
        }

        return new OnlyNetworkStrategy().request(chain);
    }
}
