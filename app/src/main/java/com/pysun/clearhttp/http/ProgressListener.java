package com.pysun.clearhttp.http;

/**
 * Created by Administrator on 2017/3/14.
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
