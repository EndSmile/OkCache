package com.xdja.cache.common.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 读取缓存，如果缓存不存在则读取网络
 */
public class CacheNetworkStrategy implements IOkCacheStrategy {
    /**
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) {
        Response response = null;
        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        try {
            response = onlyCacheStrategy.request(chain);
            if (!response.isSuccessful()) {
                return onlyNetworkStrategy.request(chain);
            }
        } catch (Exception e) {
            try {
                response = onlyNetworkStrategy.request(chain);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }
}
