package com.xdja.okcache.retrofitApi;

import com.xdja.okcache.bean.Contributor;
import com.xdja.okcache.constant.HeaderParams;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner
            , @Path("repo") String repo
            , @Query("cacheTime") int cacheTime
            , @Header(HeaderParams.CACHE_STRATEGY) int requestCacheType);

}
