package com.xdja.okcache.common.constant;

import com.xdja.okcache.common.strategy.custom.ByStaleStrategy;

/**
 * Created by ldy on 2017/8/28.
 *
 * 相对于单个的url来说，一般不会改变的参数，将参数固定至url查询参数中</p>
 *
 */

public class UrlKey {
    /**
     * 最大过期时间，以秒为单位，{@link ByStaleStrategy}使用此参数
     */
    public static final String MAX_STALE = "okcache_maxStale";

    /**
     * 是否开启缓存，优先级最高，不传此参数则默认使用初始化时的参数数据
     */
    public static final String ENABLE_CACHE = "okcache_enableCache";
}
