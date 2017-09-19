package com.xdja.okcache;

import android.support.annotation.Nullable;

import okhttp3.Request;

/**
 * Created by ldy on 2017/9/12.
 *
 * 根据request生成key的接口
 */

public interface KeyGenerator {
    /**
     * 根据request生成key，可返回null为生成失败
     */
    @Nullable
    String generateKey(Request request);
}
