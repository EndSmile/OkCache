package com.xdja.okcache.interceptor;


import com.xdja.okcache.OkCache;
import com.xdja.okcache.constant.CacheStrategy;
import com.xdja.okcache.constant.HeaderParams;
import com.xdja.okcache.strategy.ByStaleStrategy;
import com.xdja.okcache.strategy.CacheElseNetworkStrategy;
import com.xdja.okcache.strategy.NetworkElseCacheStrategy;
import com.xdja.okcache.strategy.OnlyCacheStrategy;
import com.xdja.okcache.strategy.OnlyNetworkStrategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ldy on 2017/8/21.
 *
 * cache实现的拦截器
 */

public class OkCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!OkCache.isEnableCache(request)) {
            //如果没有开启缓存则去除自定义参数结束
            return chain.proceed(OkCache.stripSelfParams(request));
        }

        String cacheTypeStr = request.header(HeaderParams.CACHE_STRATEGY);

        //缓存类型默认是CACHE_ELSE_NETWORK
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
