package com.example.nhu.api;



import com.example.nhu.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("top-headlines")
    Call<News> getNews(
            @Query("apikey") String api
    );
    @GET("search")
    Call<News> getSearchNews(
            @Query("apikey") String api,
            @Query("title") String title

    );
}
