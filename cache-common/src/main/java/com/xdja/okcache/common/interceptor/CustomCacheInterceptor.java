package com.xdja.okcache.common.interceptor;


import com.xdja.okcache.common.OkCache;
import com.xdja.okcache.common.constant.CacheStrategy;
import com.xdja.okcache.common.strategy.custom.ByStaleStrategy;
import com.xdja.okcache.common.strategy.custom.CacheElseNetworkStrategy;
import com.xdja.okcache.common.strategy.custom.NetworkElseCacheStrategy;
import com.xdja.okcache.common.strategy.custom.OnlyCacheStrategy;
import com.xdja.okcache.common.strategy.custom.OnlyNetworkStrategy;
import com.xdja.okcache.common.utils.OkCacheParamsKey;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ldy on 2017/8/21.
 */

public class CustomCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!OkCache.isEnableCache(request)) {
            //如果没有开启缓存则去除自定义参数结束
            return chain.proceed(OkCache.stripSelfParams(request));
        }

        String cacheTypeStr = request.header(OkCacheParamsKey.CACHE_STRATEGY_HEADER);

        int cacheType = CacheStrategy.CACHE_ELSE_NETWORK;
        if (cacheTypeStr != null) {
            cacheType = Integer.valueOf(cacheTypeStr);
        }
        switch (cacheType) {
            case CacheStrategy.ONLY_CACHE:
                return new OnlyCacheStrategy().request(chain);
            case CacheStrategy.ONLY_NETWORK:
                return new OnlyNetworkStrategy().request(chain);
            case CacheStrategy.NETWORK_ELSE_CACHE:
                return new NetworkElseCacheStrategy().request(chain);
            case CacheStrategy.CACHE_ELSE_NETWORK:
                return new CacheElseNetworkStrategy().request(chain);
            case CacheStrategy.BY_STALE:
                return new ByStaleStrategy().request(chain);
        }

        return chain.proceed(request);
    }
}
