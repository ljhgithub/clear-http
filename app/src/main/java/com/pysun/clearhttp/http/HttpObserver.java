package com.pysun.clearhttp.http;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;

/**
 * Created by Administrator on 2017/3/13.
 */

public class HttpObserver<T> implements Observer<T>, Disposable {
    Disposable disposable;
    HttpCallback<? super T> httpCallback;
    String tag = "";

    public HttpObserver(HttpCallback<? super T> callback, String tag) {
        this.httpCallback = callback;
        this.tag = tag;
    }

    public HttpObserver(HttpCallback<? super T> callback) {
        this(callback, "");
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
        httpCallback.onStart(tag);
        Log.d("tag", d.hashCode()+" --- "+d.isDisposed()+" onSubscribe -- " + Thread.currentThread().getName());
    }

    @Override
    public void onNext(T value) {
        if (!isDisposed()) {
            Log.d("tag",disposable.hashCode()+" "+disposable.isDisposed()+" onNext " + Thread.currentThread().getName() + value);
            try {
                httpCallback.onSuccess(value);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                onError(e);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        Log.d("tag", " onError " + Thread.currentThread().getName() + e.getMessage()+disposable.isDisposed());
        httpCallback.onError(-1, e.getMessage());
        if (!isDisposed()) {
            dispose();

        }
    }

    @Override
    public void onComplete() {
        Log.d("tag", disposable.hashCode()+" onComplete "+disposable.isDisposed());
        httpCallback.onComplete();
        if (!isDisposed()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }
}
