package com.liuxn.ambi.rocketmq;

import com.liuxn.ambi.rocketmq.runner.ProducerLoader;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * 生成者配置
 *
 * @author liuxn
 * @date 2021/10/27
 */
public abstract class ProducerManager {

    /**
     * 生产组名
     *
     * @return groupName
     */
    public abstract String groupName();

    /**
     * 发送失败重试次数
     *
     * @return retryCount
     */
    public abstract Integer retryCount();

    /***
     * 发送主题
     * @return topic
     */
    public abstract String topic();

    /***
     * 发送tag
     * @return tag
     */
    public abstract String tag();

    /**
     * 发送异步消息
     *
     * @param body     消息内容
     * @param callback 回调方法
     *
     * @throws Exception 发送异常
     */
    public void send(String body, SendCallback callback) throws Exception {
        Message message = new Message(topic(), tag(), body.getBytes(StandardCharsets.UTF_8));
        DefaultMQProducer producer = ProducerLoader.producerMap.get(groupName());
        producer.send(message, callback);
    }

    /**
     * 发送同步消息
     *
     * @param body 消息内容
     *
     * @return 发送结构
     * @throws Exception 发送异常
     */
    public SendResult send(String body) throws Exception {
        Message message = new Message(topic(), tag(), body.getBytes(StandardCharsets.UTF_8));
        DefaultMQProducer producer = ProducerLoader.producerMap.get(groupName());
        return producer.send(message);
    }


}
