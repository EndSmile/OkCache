package com.xdja.cache.retrofit;

import com.xdja.cache.retrofit.bean.Contributor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    //    @Headers("Cache-Control: max-age=640000")
    @GET("/repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> contributors(@Path("owner") String owner,
                                         @Path("repo") String repo,
                                         @Query("cacheTime") int cacheTime,
                                         @Query("isUseCache") boolean isUseCache);

}
