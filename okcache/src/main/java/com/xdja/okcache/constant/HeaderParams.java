package com.xdja.okcache.constant;

import okhttp3.Request;

/**
 * Created by ldy on 2017/8/28.
 *
 * 即使相对于单个的url来说，其值也是可以改变的，将参数放入HttpHeader中
 */

public class HeaderParams {

    /**
     * 缓存策略，其值详见{@link CacheStrategy}
     */
    public static final String CACHE_STRATEGY = "okcache_cacheStrategy";//请求缓存类型

    public static Request.Builder setCacheStrategy(Request.Builder builder,int cacheStrategy){
        if (!CacheStrategy.isValidStrategy(cacheStrategy)){
            return builder;
        }
        return builder.removeHeader(CACHE_STRATEGY)
                .addHeader(CACHE_STRATEGY,String.valueOf(cacheStrategy));
    }

    public static Request.Builder stripHeader(Request.Builder requestBuilder){
        return requestBuilder
                .removeHeader(HeaderParams.CACHE_STRATEGY);
    }
}
