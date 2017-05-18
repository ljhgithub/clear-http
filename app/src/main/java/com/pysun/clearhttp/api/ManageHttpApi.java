package com.pysun.clearhttp.api;

import android.os.Environment;
import android.util.Log;

import com.google.gson.JsonObject;
import com.pysun.clearhttp.http.HttpCallback;
import com.pysun.clearhttp.http.HttpObserver;
import com.pysun.clearhttp.http.HttpUtil;
import com.pysun.clearhttp.http.ProgressHttpCallback;
import com.pysun.clearhttp.http.RangeFunction;
import com.pysun.clearhttp.model.RangeManager;
import com.pysun.clearhttp.util.MD5;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * Created by Administrator on 2017/3/10.
 */

public class ManageHttpApi {

    public final static String LOGIN = "login";
    public final static String IMAGE = "image";
    public final static String DOWNLOAD_APK = "download_apk";

    private ManageHttpApi() {

    }

    public static ManageHttpApi getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final ManageHttpApi INSTANCE = new ManageHttpApi();

    }


    public void login(String account, String password, HttpCallback<JsonObject> httpCallback) {
        ManageHttpRequest.getInstance().httpApiJSON(LOGIN, buildTag(account, password), HttpUtil.getHttpService().login(), httpCallback);
    }

    public void login(String account, String password) {
        login(LOGIN, buildTag(account, password), ManageHttpRequest.EMPTY_CALLBACK_JSON);
    }

    public void image(String url, ProgressHttpCallback<ResponseBody> httpCallback) {
        ManageHttpRequest.getInstance().httpApi(IMAGE, buildTag(url), HttpUtil.newHttpService(httpCallback).image(url), httpCallback);
    }


    public RangeManager download(String url, ProgressHttpCallback<File> httpCallback) {
        String sharedKey = MD5.getStringMD5(url);
        String tag = buildTag(url);
        RangeManager rangeModel = new RangeManager(url);
        rangeModel.setApi(DOWNLOAD_APK);
        rangeModel.setTag(tag);
        rangeModel.setProgressHttpCallback(httpCallback);
        long range = rangeModel.getCurrentLength();
        String fileName = sharedKey;
        Log.d("tag", "range =" + rangeModel.getCurrentLength());
        Observable<Response<ResponseBody>> observable = ManageHttpRequest.getInstance().httpApiObservable(DOWNLOAD_APK, tag, HttpUtil.getHttpService().download("bytes=" + range + "-", url), httpCallback);
        if (null == observable) return null;
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        RangeFunction function = new RangeFunction(rangeModel, filePath);
        observable
                .observeOn(Schedulers.io())
                .map(function)
                .map(new Function<RangeManager, File>() {
                    @Override
                    public File apply(RangeManager rangeManager) throws Exception {
//                        Log.d("tag", "range " + rangeManager.isPause());
                        if (rangeManager.isSuccess()) {
//                            Log.d("tag", "range " + rangeManager.getFilePath());
                        } else {
                            //TODO

                        }
                        return new File(rangeManager.getFilePath());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpObserver<File>(httpCallback));

        return rangeModel;
    }


    private String buildTag(String... params) {
        String tag = "";
        for (int i = 0; i < params.length; i++) {
            tag += params[i];
        }
        return tag;
    }


}
