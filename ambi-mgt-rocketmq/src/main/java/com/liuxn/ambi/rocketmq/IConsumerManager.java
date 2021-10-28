package com.liuxn.ambi.rocketmq;

import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;

/**
 * 消费者监听
 *
 * @author liuxn
 * @date 2021/10/27
 */
public interface IConsumerManager extends MessageListenerConcurrently {

    /**
     * 主题
     *
     * @return topic
     */
    String topic();

    /**
     * tags
     *
     * @return tag
     */
    String tags();

    /**
     * 消费者组名
     *
     * @return groupName
     */
    String groupName();

}
