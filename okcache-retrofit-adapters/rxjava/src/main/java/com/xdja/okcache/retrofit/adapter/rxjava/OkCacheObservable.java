package com.xdja.okcache.retrofit.adapter.rxjava;


import rx.Observable;
import rx.plugins.RxJavaHooks;

/**
 * Created by endsmile on 2017/9/4.
 */
public class OkCacheObservable<T> extends Observable<T> {

    /**
     * {@inheritDoc}
     */
    protected OkCacheObservable(OnSubscribe f) {
        super(f);
    }

    public static <T> OkCacheObservable<T> create(OnSubscribe<T> f) {
        return new OkCacheObservable<T>(RxJavaHooks.onCreate(f));
    }


}
