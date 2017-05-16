package com.pysun.clearhttp.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/3/10.
 */

public class HttpUtil {
    private static Retrofit retrofit;
    private static HttpService httpService;
    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        httpService = retrofit.create(HttpService.class);

    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static HttpService getHttpService() {
        return httpService;
    }

    public static HttpService newHttpService(final ProgressListener progressListener) {
        OkHttpClient httpClient = client.newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponseBody(response.body(), progressListener)).build();
            }
        })
                .build();

        return retrofit.newBuilder().client(httpClient).build().create(HttpService.class);
    }
}
