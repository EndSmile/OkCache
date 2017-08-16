package com.xdja.cache.okhttp.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * FIXME
 *
 */
public interface IRequestStrategy {
    /**
     * 请求策略
     * @param chain
     * @return
     */
   Response request(Interceptor.Chain chain) throws IOException;
}
