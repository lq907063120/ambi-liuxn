package com.liuxn.ambi.web.controller;

import com.liuxn.ambi.web.mq.TestProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuxn
 * @date 2021/10/25
 */
@RestController
public class TestController {

    @Autowired
    private TestProducer testProducer;

    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }

    @RequestMapping("/test/send_mq")
    public String sendMq(String msg) throws Exception {


        testProducer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {

            }

            @Override
            public void onException(Throwable e) {

            }
        });
        return "成功";


    }
}
