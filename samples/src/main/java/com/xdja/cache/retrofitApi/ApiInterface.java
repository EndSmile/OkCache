package com.xdja.cache.retrofitApi;

import com.xdja.cache.bean.Contributor;
import com.xdja.cache.common.utils.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner
                                        ,@Path("repo") String repo
                                        ,@Header(Common.REQUEST_CACHE_TYPE_HEAD) int requestCacheType);

}
