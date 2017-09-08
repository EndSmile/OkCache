package com.xdja.okcache.retrofit.adapter.rxjava.callintercepter;

import java.security.InvalidParameterException;

/**
 * Created by ldy on 2017/9/7.
 */

public class CallInterceptorContainer{
    private CallInterceptor callInterceptor;

    public CallInterceptor getCallInterceptor() {
        return callInterceptor;
    }

    public void setCallInterceptor(CallInterceptor callInterceptor) {
        this.callInterceptor = callInterceptor;
    }
}
