package com.xdja.cache.common.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 先进行网络请求，如果网络请求失败则直接请求缓存数据
 */
public class NetworkCacheStrategy implements IRequestStrategy {
    private float mMaxStale;//缓存过期时间

    public NetworkCacheStrategy(float mMaxStale) {
        this.mMaxStale = mMaxStale;
    }

    /**
     * 请求策略
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
        Response response = null;
        CacheStrategy cacheStrategy = new CacheStrategy(mMaxStale);
        OnlyNetworkStrategy networkStrategy = new OnlyNetworkStrategy();
        try {
            response = networkStrategy.request(chain);
            if(!response.isSuccessful()){
                return cacheStrategy.request(chain);
            }
        } catch (Exception e){
            try {
                response = cacheStrategy.request(chain);
            } catch (IOException e1) {
                //忽略不处理
            }
        }
        return response;
    }
}
