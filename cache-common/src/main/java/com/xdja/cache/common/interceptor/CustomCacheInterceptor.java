package com.xdja.cache.common.interceptor;


import com.xdja.cache.common.utils.Common;

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
        String enableCacheStr = request.header("enableCache");
        String cacheTypeStr = request.header(Common.REQUEST_CACHE_TYPE_HEAD);
        boolean enableCache = Boolean.valueOf(enableCacheStr);
        if (!enableCache) {
            return chain.proceed(request);
        }

        int cacheType = CacheType.CACHE_ELSE_NETWORK;
        if (cacheTypeStr != null) {
            cacheType = Integer.valueOf(cacheTypeStr);
        }
        switch (cacheType) {
            case CacheType.ONLY_CACHE:

        }

        return null;
    }
}
