package com.abc.localproduceapp;

import android.webkit.JsPromptResult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("/")
    Call<Void> start();
    @POST("/check")
    Call<Void> check(@Header("token") String token);
    @POST("/login")
    Call<JsonElement> login(@Body User user);
    @POST("/signup")
    Call<JsonElement> signup(@Body User user);
    @POST("/update")
    Call<JsonElement> update(@Body JsonElement json,@Header("token") String tk);
    @POST("/token")
    Call<JsonElement> mainactivity(@Body JsonElement token,@Header("token") String tk);
    @POST("/additem")
    Call<String> additem(@Body JsonElement json,@Header("token") String tk);
    @GET("/getitem")
    Call<JsonElement> getitem(@Query("itemtoken") String tkn,@Header("token") String tk);
    @GET("/listitem")
    Call<JsonElement> listitems(@Header("token") String tk);
}


