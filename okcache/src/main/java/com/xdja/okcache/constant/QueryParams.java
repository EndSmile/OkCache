package com.xdja.okcache.constant;

import com.xdja.okcache.strategy.ByStaleStrategy;

import okhttp3.HttpUrl;

/**
 * Created by ldy on 2017/8/28.
 * <p>
 * 相对于单个的url来说，一般不会改变的参数，将参数固定至url查询参数中</p>
 */

public class QueryParams {

    /**
     * 最大过期时间，以秒为单位，{@link ByStaleStrategy}使用此参数
     */
    public static final String MAX_STALE = "okcache_maxStale";

    /**
     * 是否开启缓存，优先级最高，不传此参数则默认使用初始化时的参数数据
     */
    public static final String ENABLE_CACHE = "okcache_enableCache";

    /**
     * 设置最大新鲜值,<=0无效
     */
    public static HttpUrl.Builder setMaxStale(HttpUrl.Builder builder, long maxStale) {
        builder.removeAllQueryParameters(MAX_STALE);

        if (maxStale <= 0) {
            return builder;
        }
        return builder.addQueryParameter(MAX_STALE, String.valueOf(maxStale));
    }

    /**
     * 设置是否开启缓存
     *
     * @param enableCache 为null不设置，并移除原有设置
     */
    public static HttpUrl.Builder setEnableCache(HttpUrl.Builder builder, Boolean enableCache) {
        builder.removeAllQueryParameters(ENABLE_CACHE);
        if (enableCache == null) {
            return builder;
        }
        return builder.addQueryParameter(ENABLE_CACHE, String.valueOf(enableCache));
    }

    public static HttpUrl stripUrlQuery(HttpUrl httpUrl) {
        return httpUrl.newBuilder()
                .removeAllQueryParameters(QueryParams.ENABLE_CACHE)
                .removeAllQueryParameters(QueryParams.MAX_STALE)
                .build();
    }
}
