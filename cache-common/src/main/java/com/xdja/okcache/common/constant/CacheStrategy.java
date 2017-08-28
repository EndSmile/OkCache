package com.xdja.okcache.common.constant;

/**
 * 缓存的几种模式
 */
public class CacheStrategy {
    /**
     * 只读取缓存
     */
    public static final int ONLY_CACHE = 1;
    /**
     * 只读取网络
     */
    public static final int ONLY_NETWORK = 2;
    /**
     * 读取缓存，如果缓存不存在则读取网络
     */
    public static final int CACHE_ELSE_NETWORK = 3;
    /**
     * 先读取网络，如果网络请求失败则读取缓存
     */
    public static final int NETWORK_ELSE_CACHE = 4;

    /**
     * 无缓存或缓存到期时使用网络，否则使用缓存
     */
    public static final int BY_STALE = 5;

    public static boolean isValidStrategy(int cacheStrategy) {
        return cacheStrategy >= 1 && cacheStrategy <= 5;
    }

}
