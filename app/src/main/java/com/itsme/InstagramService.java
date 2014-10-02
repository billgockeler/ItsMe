package com.itsme;

import com.itsme.model.SearchResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface InstagramService {

    @GET("/tags/{tag}/media/recent?client_id=" + Constants.INSTAGRAM_CLIENT_ID + "&count=42")
    void searchForTag(@Path("tag") String tag, Callback<SearchResult> tagResult);

    @GET("/tags/{tag}/media/recent?client_id=" + Constants.INSTAGRAM_CLIENT_ID + "&count=42")
    void searchForTag(@Path("tag") String tag, @Query("max_tag_id") String max_id, Callback<SearchResult> tagResult);

}