package com.xdja.cache.common.strategy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP请求策略
 */
public class RequestStrategy {

    private IOkCacheStrategy mICacheStrategy;

    public RequestStrategy(){

    }

    public RequestStrategy(IOkCacheStrategy iCacheStrategy){
        this.mICacheStrategy = iCacheStrategy;
    }

    public void setBaseRequestStrategy(IOkCacheStrategy iCacheStrategy) {
        mICacheStrategy = iCacheStrategy;
    }

    public Response request(Interceptor.Chain chain) throws IOException {
        Response response = null;
        if(mICacheStrategy !=null){
            response = mICacheStrategy.request(chain);
        }
        if(response==null){
            //确认response不能返回NULL 否则会抛出空指针异常,这里做一个处理
            Request request = chain.request();
            response = chain.proceed(request);
        }
        return response;
    }
}
