package com.xdja.okcache.retrofitApi;


import com.xdja.okcache.retrofit.generator.RetrofitCacheGenerator;

public class ApiFactory {

    private RetrofitCacheGenerator retrofitCacheGenerator;

    public ApiFactory() {
        this.retrofitCacheGenerator = new RetrofitCacheGenerator();
    }


    public ApiInterface getCacheApi() {
        return this.retrofitCacheGenerator.createService(ApiInterface.class);
    }
}
