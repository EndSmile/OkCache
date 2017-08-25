package com.xdja.cache.common.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 先进行网络请求，如果网络请求失败则直接请求缓存数据
 */
public class NetworkCacheStrategy implements IOkCacheStrategy {
    /**
     * 请求策略
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
        Response response = null;
        OnlyCacheStrategy onlyCacheStrategy = new OnlyCacheStrategy();
        OnlyNetworkStrategy onlyNetworkStrategy = new OnlyNetworkStrategy();
        try {
            response = onlyNetworkStrategy.request(chain);
            if(!response.isSuccessful()){
                return onlyCacheStrategy.request(chain);
            }
        } catch (Exception e){
            try {
                response = onlyCacheStrategy.request(chain);
            } catch (IOException e1) {
                //忽略不处理
            }
        }
        return response;
    }
}
