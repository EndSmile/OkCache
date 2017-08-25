package com.xdja.cache.common.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 */
public interface IOkCacheStrategy {
    /**
     * 请求策略
     * @param chain
     * @return
     */
   Response request(Interceptor.Chain chain) throws IOException;
}
