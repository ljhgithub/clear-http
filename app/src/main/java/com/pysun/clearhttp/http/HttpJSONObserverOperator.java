package com.pysun.clearhttp.http;

import android.util.Log;

import com.google.gson.JsonObject;
import com.pysun.clearhttp.api.ManageHttpRequest;

import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/3/15.
 */

public class HttpJSONObserverOperator implements ObservableOperator<JsonObject, JsonObject> {
    private HttpCallback<JsonObject> httpCallback;
    private HttpJSONObserverOperator(){

        this.httpCallback= ManageHttpRequest.EMPTY_CALLBACK_JSON;
    }
    public HttpJSONObserverOperator(HttpCallback<JsonObject> httpCallback){
        this.httpCallback=httpCallback;
    }
    @Override
    public Observer<? super JsonObject> apply(final Observer<? super JsonObject> observer) throws Exception {
        return new Observer<JsonObject>() {
          Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable=d;
                Log.d("tag","onSubscribe1 "+disposable.hashCode());

            }

            @Override
            public void onNext(JsonObject value) {
                Log.d("tag","onNext1 "+disposable.hashCode()+" "+disposable.isDisposed()+value.toString());

                //TODO 解析数据

                if (false){
                    httpCallback.onError(1,"error");
                }else {
                    observer.onNext(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("tag","onError1 "+disposable.hashCode()+" "+e.toString());
                observer.onError(e);
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                observer.onComplete();
                disposable.dispose();
                Log.d("tag","onComplete1 "+disposable.hashCode()+" "+disposable.isDisposed());

            }
        };
    }
}
