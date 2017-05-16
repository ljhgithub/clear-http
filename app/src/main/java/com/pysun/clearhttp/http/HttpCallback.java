package com.pysun.clearhttp.http;
/**
 * Created by Administrator on 2017/3/13.
 */

public interface HttpCallback<T> {
    void onStart(String tag);
    void onSuccess(T t);
    void onError(int code, String msg);
    void onComplete();
}
