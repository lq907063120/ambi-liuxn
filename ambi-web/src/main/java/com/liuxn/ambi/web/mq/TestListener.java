package com.liuxn.ambi.web.mq;

import com.liuxn.ambi.rocketmq.IConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuxn
 * @date 2021/10/27
 */
@Component
@Slf4j
public class TestListener implements IConsumerManager {
    @Override
    public String topic() {
        return "xs-test-black";
    }

    @Override
    public String tags() {
        return "*";
    }

    @Override
    public String groupName() {
        return "test_consumer_group";
    }


//    @Autowired
//    private TestService testService;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
        log.info(Thread.currentThread().getName() + " Receive New Messages: " + list.size());
        MessageExt message = list.get(0);
        if (message == null) {
            log.error("处理消息时, message 为空");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        log.info("处理消息 , topic={}, tags={}, åkeys={}, reConsumeTimes={}", message.getTopic(), message.getTags(), message.getKeys(), message.getReconsumeTimes());
        try {
            String tag = message.getTags();
            String msgId = message.getMsgId();
            String body = new String(message.getBody());
            log.info("处理MQ消息::::" + msgId + "-----" + body);
            // testService.outMessage(body);

            log.info("处理MQ消息消费 成功 , topic={}, tag={}, key={}, reConsumeTimes={}", message.getTopic(), message.getTags(), message.getKeys(), message.getReconsumeTimes());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("处理MQ消息消费 保存记录失败，topic={}, tag={}, key={}, reConsumeTimes={}, body={}，e={}",
                    message.getTopic(), message.getTags(), message.getKeys(), message.getReconsumeTimes(), new String(message.getBody()), e);
            if (message.getReconsumeTimes() < 5) {
                log.info("处理MQ，准备重新消费消息");
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } else {
                // 超过5次，就不重发了
                //todo 提醒
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

        }
    }
}
