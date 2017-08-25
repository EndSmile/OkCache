package com.xdja.cache.common.strategy.custom;

import com.xdja.cache.common.OkCache;
import com.xdja.cache.common.strategy.IRequestStrategy;
import com.xdja.cache.common.utils.OkCacheParamsKey;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * 仅仅请求缓存策略
 */
public class OnlyCacheStrategy implements IRequestStrategy {

    /**
     * 请求策略
     *
     * @param chain
     * @return
     */
    @Override
    public Response request(Interceptor.Chain chain) throws IOException {
        Request request = OkCache.stripSelfParams(chain.request());

        Response response = OkCache.getCacheOperation().get(request);
        if (response==null){
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(504)
                    .message("Unsatisfiable Request (only-if-cached)")
                    .body(Util.EMPTY_RESPONSE)
                    .sentRequestAtMillis(-1L)
                    .receivedResponseAtMillis(System.currentTimeMillis())
                    .build();
        }
        return response;
    }
}
