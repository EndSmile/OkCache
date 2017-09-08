package com.xdja.okcache.retrofit.adapter.rxjava.strategy;

import com.xdja.okcache.constant.CacheStrategy;

/**
 * Created by ldy on 2017/9/4.
 */

public enum AdvanceCacheStrategy {
    //基础策略，参见 {@link CacheStrategy}
    ONLY_CACHE,ONLY_NETWORK,CACHE_ELSE_NETWORK,NETWORK_ELSE_CACHE,BY_STALE,

    /**
     * 请求缓存的同时请求网络
     */
    CACHE_WHILE_NETWORK,
    /**
     * 请求缓存的同时请求网络但是网络并不更新
     */
    CACHE_WHILE_HIDE_NETWORK
}
