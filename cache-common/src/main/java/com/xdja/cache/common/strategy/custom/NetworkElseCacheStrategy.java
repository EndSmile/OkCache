package com.xdja.cache.common.strategy.custom;

import com.xdja.cache.common.strategy.IRequestStrategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 先进行网络请求，如果网络请求失败则直接请求缓存数据
 */
public class NetworkElseCacheStrategy implements IRequestStrategy {

    /**
     * 请求策略
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
        Response netResponse = new OnlyNetworkStrategy().request(chain);
        if (!netResponse.isSuccessful()){
            Response cacheResponse = new OnlyCacheStrategy().request(chain);
            if (cacheResponse.isSuccessful()){
                return cacheResponse;
            }
        }
        return netResponse;
    }
}
