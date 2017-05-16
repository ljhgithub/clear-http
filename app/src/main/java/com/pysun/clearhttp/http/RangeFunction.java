package com.pysun.clearhttp.http;


import com.pysun.clearhttp.model.RangeManager;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/4/1.
 */

public class RangeFunction implements Function<Response<ResponseBody>,RangeManager> {

   private RangeManager rangeModel;
    private String path;
    public RangeFunction(RangeManager rangeModel, String path){
        this.rangeModel=rangeModel;
        this.path=path;
    }
    @Override
    public RangeManager apply(Response<ResponseBody> response) throws Exception {

        if (rangeModel.getCurrentLength() == 0) {
            rangeModel.setTotalLength(response.body().contentLength());
            rangeModel.setFilePath(path);

        }
        rangeModel.readBody(response.body().byteStream());
        return rangeModel;
    }
}
