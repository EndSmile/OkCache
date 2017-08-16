package com.xdja.cache.okhttp.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 读取缓存，如果缓存不存在则读取网络
 *
 */
public class CacheNetworkStrategy implements IRequestStrategy {
    /**
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) {
        Response response = null;
        CacheStrategy cacheStrategy = new CacheStrategy();
        NetworkStrategy networkStrategy = new NetworkStrategy();
        try {
            response = cacheStrategy.request(chain);
            if (!response.isSuccessful()) {
                return networkStrategy.request(chain);
            }
        } catch (Exception e) {
            try {
                response = networkStrategy.request(chain);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }
}
