package com.xdja.okcache.common.constant;

/**
 * Created by ldy on 2017/8/28.
 *
 * 即使相对于单个的url来说，其值也是可以改变的，将参数放入HttpHeader中
 */

public class HeaderKey {
    /**
     * 缓存策略，其值详见{@link CacheStrategy}
     */
    public static final String CACHE_STRATEGY = "okcache_cacheStrategy";//请求缓存类型

}
