package com.pysun.clearhttp.api;

import com.google.gson.JsonObject;
import com.pysun.clearhttp.http.HttpCallback;
import com.pysun.clearhttp.http.HttpJSONObserverOperator;
import com.pysun.clearhttp.http.HttpObserver;
import com.pysun.clearhttp.http.ProgressHttpCallback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/10.
 */

public class ManageHttpRequest {


    private ManageHttpRequest() {

    }

    public static ManageHttpRequest getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final ManageHttpRequest INSTANCE = new ManageHttpRequest();

    }

    final static Map<String, Map<String, Disposable>> maps = new ConcurrentHashMap<>();//TODO 需要控制map容量


    public void cancelAll() {
        for (Map.Entry<String, Map<String, Disposable>> entry : maps.entrySet()) {
            for (Disposable disposable : entry.getValue().values()) {
                disposable.dispose();
            }
        }
    }


    public void cancelByApi(String api) {
        if (maps.containsKey(api)) {
            for (Disposable disposable : maps.get(api).values()) {
                disposable.dispose();
            }
        }
    }

    public void cancelByTag(String api, String tag) {
        if (maps.containsKey(api) && maps.get(api).containsKey(tag)) {
            maps.get(api).get(tag).dispose();
        }
    }

    public void clear() {
        cancelAll();
        maps.clear();
    }


    public void httpApiJSON(String api, String tag, Observable<JsonObject> objectObservable, HttpCallback<JsonObject> httpCallback) {
        httpApi(api, tag, objectObservable.lift(new HttpJSONObserverOperator(httpCallback)), httpCallback);
    }

    public <T> void httpApi(String api, String tag, Observable<T> objectObservable, HttpCallback<T> httpCallback) {
        distinct(api, tag, objectObservable, httpCallback);

    }

    public Observable<JsonObject> httpApiResultJSON(String api, String tag, Observable<JsonObject> objectObservable, HttpCallback<JsonObject> httpCallback) {
        return httpApiObservable(api, tag, objectObservable.lift(new HttpJSONObserverOperator(httpCallback)), httpCallback);
    }

    public <T,R> Observable<T> httpApiObservable(String api, String tag, Observable<T> objectObservable, HttpCallback<R> httpCallback) {
        return distinctResult(api, tag, objectObservable, httpCallback);

    }


    /**
     * 过滤重复请求
     * @param api
     * @param tag
     * @param objectObservable
     * @param httpCallback
     * @param <T>
     */
    private  <T> void distinct(String api, String tag, Observable<T> objectObservable, HttpCallback<T> httpCallback) {
        ObjectHelper.requireNonNull(httpCallback, "HttpCallback is null");
        final Map<String, Disposable> map;
        final String finalTag = tag;
        map = getTagMap(api);
        if (map.containsKey(tag) && !map.get(tag).isDisposed()) {
            httpCallback.onError(-2, "重复请求！");

        } else {
            objectObservable
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
//                        Log.d("tag", " doOnSubscribe " + Thread.currentThread().getName());
                            map.put(finalTag, disposable);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpObserver<>(httpCallback, tag));
        }

    }

    /**
     * 过滤重复请求
     * @param api
     * @param tag
     * @param objectObservable
     * @param httpCallback
     * @param <T>
     * @param <R>
     * @return
     */
    private  <T,R> Observable<T> distinctResult(String api, String tag, Observable<T> objectObservable, HttpCallback<R> httpCallback) {
        ObjectHelper.requireNonNull(httpCallback, "HttpCallback is null");
        final Map<String, Disposable> map;
        final String finalTag = tag;

        map = getTagMap(api);
        if (map.containsKey(tag) && !map.get(tag).isDisposed()) {
            httpCallback.onError(-2, "重复请求！");
            return null;

        } else {
            return objectObservable
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
//                        Log.d("tag", " doOnSubscribe " + Thread.currentThread().getName());
                            map.put(finalTag, disposable);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread());


        }

    }

    private Map<String, Disposable> getTagMap(String api) {
        Map<String, Disposable> map;
        if (maps.containsKey(api)) {
            map = maps.get(api);
            return map;
        } else {
            map = new ConcurrentHashMap<>();
            maps.put(api, map);
            return map;
        }

    }


    public static final HttpCallback<Object> EMPTY_CALLBACK = new HttpCallback<Object>() {

        @Override
        public void onStart(String tag) {

        }

        @Override
        public void onSuccess(Object o) {

        }

        @Override
        public void onError(int code, String msg) {

        }

        @Override
        public void onComplete() {

        }
    };

    public static final HttpCallback<JsonObject> EMPTY_CALLBACK_JSON = new HttpCallback<JsonObject>() {

        @Override
        public void onStart(String tag) {

        }

        @Override
        public void onSuccess(JsonObject o) {

        }

        @Override
        public void onError(int code, String msg) {

        }

        @Override
        public void onComplete() {

        }
    };

    public static final ProgressHttpCallback EMPTY_PROGRESS_CALLBACK=new ProgressHttpCallback() {
        @Override
        public void onStart(String tag) {

        }

        @Override
        public void onSuccess(Object o) {

        }

        @Override
        public void onError(int code, String msg) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {

        }
    };
}
