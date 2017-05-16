package com.pysun.clearhttp.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Administrator on 2017/3/14.
 */

public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody originalResponseBody;
    private BufferedSource bufferedSource;
    private final ProgressListener progressListener;

    public ProgressResponseBody(ResponseBody body, ProgressListener progressListener) {
        Log.d("tag", "ProgressResponseBody " + " " + body.contentLength());
        this.originalResponseBody = body;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return originalResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return originalResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(originalResponseBody.source()));
        }
        return bufferedSource;
    }


    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                Log.d("tag", "totalBytesRead " + totalBytesRead + " " + contentLength());
                progressListener.update(totalBytesRead, originalResponseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }

}
