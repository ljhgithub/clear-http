package com.pysun.clearhttp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.pysun.clearhttp.api.ManageHttpApi;
import com.pysun.clearhttp.http.ProgressHttpCallback;
import com.pysun.clearhttp.model.RangeManager;

import java.io.File;

public class MainActivity extends Activity implements View.OnClickListener {
    Integer i = 0;
    ImageView imageView;
    ProgressBar progressBar;
    private TextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (TextView) findViewById(R.id.start);
        start.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        start.setSelected(false);

    }


    private RangeManager downloadApk() {

        return ManageHttpApi.getInstance().download("http://gdown.baidu.com/data/wisegame/3bd0d6f36475678b/baidushoujizhushou_16793039.apk", new ProgressHttpCallback<File>() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                progressBar.setProgress((int) (((bytesRead + 0.0) / contentLength) * 1000));
            }

            @Override
            public void onStart(String tag) {
                Log.d("tag", "onStart ");
            }

            @Override
            public void onSuccess(File body) {
                Log.d("tag", body.length() + body.getAbsolutePath());
                start.setText("开始");
                start.setSelected(false);
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("tag", "code " + code + " " + msg);
            }
            @Override
            public void onComplete() {
                Log.d("tag", "onComplete ");

            }
        });
    }


    RangeManager rangeModel;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (start.isSelected()) {
                    rangeModel.pause();
                    start.setText("开始");
                    return;
                } else {
                    start.setText("暂停");
                }
                rangeModel = downloadApk();
                start.setSelected(!start.isSelected());

                break;
            default:
                break;
        }
    }
}