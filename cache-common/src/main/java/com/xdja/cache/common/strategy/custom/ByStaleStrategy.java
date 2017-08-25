package com.xdja.cache.common.strategy.custom;

import com.xdja.cache.common.OkCache;
import com.xdja.cache.common.strategy.IRequestStrategy;
import com.xdja.cache.common.utils.OkCacheParamsKey;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkCacheOperation;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ldy on 2017/8/25.
 *
 * 如果有缓存且缓存未过期则使用缓存
 */

public class ByStaleStrategy implements IRequestStrategy {
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {

        Request request = chain.request();
        String maxStaleStr = request.url().queryParameter(OkCacheParamsKey.MAX_STALE_URL);

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
