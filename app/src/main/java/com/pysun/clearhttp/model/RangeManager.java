package com.pysun.clearhttp.model;

import android.util.Log;


import com.pysun.clearhttp.api.ManageHttpRequest;
import com.pysun.clearhttp.http.ProgressHttpCallback;
import com.pysun.clearhttp.util.AppUtil;
import com.pysun.clearhttp.util.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2017/4/1.
 */

public class RangeManager {
    public ProgressHttpCallback getProgressHttpCallback() {
        return progressHttpCallback;
    }

    public void setProgressHttpCallback(ProgressHttpCallback progressHttpCallback) {
        this.progressHttpCallback = progressHttpCallback;
    }

    private ProgressHttpCallback progressHttpCallback;
    private boolean pause = false;
    private boolean cancel = false;
    private boolean success = false;

    private long totalLength;
    private long currentLength;
    private String filePath;

    private String key;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;
    private String api;
    private String tag;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public RangeManager() {

    }

    public RangeManager(String url) {
        this.url=url;
        key = MD5.getStringMD5(url);
        String str = AppUtil.getSharedPreferences().getString(key, "");
        try {
            JSONObject jsonObject = new JSONObject(str);
            this.totalLength = jsonObject.optLong("totalLength", 0);
            this.currentLength = jsonObject.optLong("currentLength", 0);
            this.filePath = jsonObject.optString("filePath");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readBody(InputStream is) {
        if (null == progressHttpCallback) {
            progressHttpCallback = ManageHttpRequest.EMPTY_PROGRESS_CALLBACK;
        }
        File file;
        if (currentLength == 0) {
            file = new File(filePath + ".part");
            for (int i = 1; (file.exists() && file.isFile()); i++) {
                file = new File(filePath + "(" + i + ")" + ".part");
            }
            filePath = file.getAbsolutePath();
        }
        file = new File(filePath);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            long fileLength = randomAccessFile.length();
            randomAccessFile.seek(fileLength);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1 && !isPause()) {
                randomAccessFile.write(buffer, 0, len);
                currentLength += len;
                progressHttpCallback.update(currentLength, totalLength, false);
            }
            if (currentLength == totalLength && totalLength > 0) {
                success = true;
                progressHttpCallback.update(currentLength, totalLength, true);
                AppUtil.getSharedPreferences().edit().remove(key).apply();
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("totalLength", totalLength);
                jsonObject.put("currentLength", currentLength);
                jsonObject.put("filePath", filePath);
                AppUtil.getSharedPreferences().edit().putString(key, jsonObject.toString()).apply();
            }

            is.close();
            randomAccessFile.close();
        } catch (Exception e) {
            Log.d("tag", e.getMessage());
            pause = true;
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("totalLength", totalLength);
                jsonObject1.put("currentLength", currentLength);
                jsonObject1.put("filePath", filePath);
                AppUtil.getSharedPreferences().edit().putString(key, jsonObject1.toString()).apply();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
    }

    public boolean isPause() {
        return pause;
    }


    public void pause(){
        this.pause=true;
        ManageHttpRequest.getInstance().cancelByTag(api,tag);
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
