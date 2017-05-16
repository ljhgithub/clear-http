package com.pysun.clearhttp.http;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/3/10.
 */

public interface HttpService {
    @GET("/")
    Observable<JsonObject> login();

    @GET
    @Streaming
    Observable<ResponseBody> image(@Url String url);


    @GET
    @Streaming
    Observable<retrofit2.Response<ResponseBody>> download(@Header("Range") String range, @Url String url);


    @GET
    @Streaming
    retrofit2.Call<ResponseBody> download1(@Header("Range") String range, @Url String url);
}
