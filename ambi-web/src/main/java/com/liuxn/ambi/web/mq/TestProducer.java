package com.liuxn.ambi.web.mq;

import com.liuxn.ambi.rocketmq.BaseProducer;
import org.springframework.stereotype.Component;

/**
 * @author liuxn
 * @date 2021/10/27
 */
@Component
public class TestProducer extends BaseProducer {


    @Override
    public String groupName() {
        return "test_producer_group";
    }

    @Override
    public Integer retryCount() {
        return 3;
    }

    @Override
    public String topic() {
        return "xs-test-black";
    }

    @Override
    public String tag() {
        return "Tag_test_black";
    }


}
