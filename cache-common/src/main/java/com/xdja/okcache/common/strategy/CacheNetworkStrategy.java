package com.xdja.okcache.common.strategy;

import com.xdja.okcache.common.strategy.custom.OnlyNetworkStrategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 读取缓存，如果缓存不存在则读取网络
 */
public class CacheNetworkStrategy implements IRequestStrategy {
    private float mMaxStale;//缓存过期时间

    public CacheNetworkStrategy(float mMaxStale) {
        this.mMaxStale = mMaxStale;
    }

    /**
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) {
        Response response = null;
        CacheStrategy cacheStrategy = new CacheStrategy(mMaxStale);
        OnlyNetworkStrategy networkStrategy = new OnlyNetworkStrategy();
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
