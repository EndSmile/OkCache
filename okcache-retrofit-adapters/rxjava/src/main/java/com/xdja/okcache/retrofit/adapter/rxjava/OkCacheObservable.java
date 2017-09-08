package com.xdja.okcache.retrofit.adapter.rxjava;


import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.CallInterceptor;
import com.xdja.okcache.retrofit.adapter.rxjava.callintercepter.CallInterceptorContainer;

import rx.Observable;
import rx.plugins.RxJavaHooks;

/**
 * Created by endsmile on 2017/9/4.
 */
public class OkCacheObservable<T> extends Observable<T> {

    private final CallInterceptorContainer container;

    /**
     * {@inheritDoc}
     */
    protected OkCacheObservable(OnSubscribe f, CallInterceptorContainer container) {
        super(f);
        this.container = container;
    }

    public static <T> OkCacheObservable<T> create(OnSubscribe<T> f, CallInterceptorContainer container) {
        return new OkCacheObservable<T>(RxJavaHooks.onCreate(f),container);
    }

    public OkCacheObservable<T> setCallInterceptor(CallInterceptor<T> callInterceptor){
        this.container.setCallInterceptor(callInterceptor);
        return this;
    }
}
