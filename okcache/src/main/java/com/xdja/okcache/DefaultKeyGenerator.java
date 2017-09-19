package com.xdja.okcache;

import android.support.annotation.Nullable;

import okhttp3.Request;

/**
 * Created by ldy on 2017/9/12.
 *
 * 默认的key生成器
 */

public class DefaultKeyGenerator implements KeyGenerator {
    @Nullable
    @Override
    public String generateKey(Request request) {
        return KeyUtil.getKey(request);
    }

}
