package com.log4j2.plugin;

import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.producer.ILogCallback;
import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.response.PutLogsResponse;


/**
 * Created by SL on 2017/2/3.
 */
public class CallbackSample extends ILogCallback {

    public LogProducer producer;
    public CallbackSample(LogProducer producer) {
        super();
        this.producer = producer;
    }

    public void onCompletion(PutLogsResponse response, LogException e) {
        if (e != null) {
            // 打印异常
            System.out.println(e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
        }
//        else{
//            System.out.println("send success, request id: " + response.GetRequestId());
//        }
    }

}
