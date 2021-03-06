package com.xdja.okcache.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 读取缓存，如果缓存不存在则读取网络
 */
public class CacheElseNetworkStrategy implements IRequestStrategy {

    /**
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException{
        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        Response cacheResponse = onlyCacheStrategy.request(chain);
        if (cacheResponse.isSuccessful()){
            return cacheResponse;
        }else {
            return new OnlyNetworkStrategy().request(chain);
        }
    }
}
