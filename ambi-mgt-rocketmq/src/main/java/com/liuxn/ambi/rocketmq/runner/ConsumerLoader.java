package com.liuxn.ambi.rocketmq.runner;

import com.liuxn.ambi.rocketmq.IConsumerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuxn
 * @date 2021/10/27
 */
@Slf4j
@Configuration
@ConditionalOnClass({MQAdmin.class})
public class ConsumerLoader implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {
    private ConfigurableApplicationContext applicationContext;


    @Value("${rocket.mq.address}")
    private String ADDRESS;

    private List<DefaultMQPushConsumer> consumerList = new ArrayList<>();


    @Override
    public void afterSingletonsInstantiated() {
        try {
            Map<String, IConsumerManager> consumerListenerMap = applicationContext.getBeansOfType(IConsumerManager.class)
                    .entrySet().stream().filter(entry -> {
                        return !ScopedProxyUtils.isScopedTarget(entry.getKey());
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (consumerListenerMap.isEmpty()) {
                return;
            }
            log.info(">> 正在初始化rocket mq consumers 监听......");
            if (StringUtils.isBlank(ADDRESS)) {
                throw new NullPointerException("rocket.mq.address not configured");
            }
            for (Map.Entry<String, IConsumerManager> entry : consumerListenerMap.entrySet()) {
                subscribe(entry.getKey(), entry.getValue());
            }
            log.info(">> 初始化rocket mq consumers 监听完毕,consumer size:[" + consumerListenerMap.size() + "]");
        } catch (Exception e) {
            throw new RuntimeException("初始化rocket mq consumer 监听异常[" + e.getMessage() + "]");
        }
    }


    private void subscribe(String beanName, IConsumerManager bean) throws MQClientException {
        String topic = bean.topic();
        String tags = StringUtils.isBlank(bean.tags()) ? "*" : bean.tags();
        String groupName = bean.groupName();
        if (StringUtils.isBlank(groupName)) {
            throw new NullPointerException("groupName is null at " + bean.getClass().getName());
        }
        if (StringUtils.isBlank(topic)) {
            throw new NullPointerException("topic is null at " + bean.getClass().getName());
        }
        log.info(">> rocket mq address:[" + ADDRESS + "],beanName:[" + beanName + "],consumer group:[" + groupName + "]");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(ADDRESS);
        consumer.subscribe(topic, tags);
        consumer.setMessageListener(bean);
        consumer.start();
        consumerList.add(consumer);
        log.info(">> rocket mq consumer [" + bean.getClass().getName() + "] 订阅 topic:[" + topic + "],tags:[" + tags + "]");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }


    @Override
    public void destroy() throws Exception {
        log.info(">> rocket mq consumer is shutdown");
        if (!consumerList.isEmpty()) {
            for (DefaultMQPushConsumer consumer : consumerList) {
                consumer.shutdown();
            }
        }
    }
}

