package com.liuxn.ambi.rocketmq.runner;

import com.liuxn.ambi.rocketmq.BaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 生产者 初始类
 *
 * @author liuxn
 * @date 2021/10/28
 */
@Slf4j
@Configuration
@ConditionalOnClass({MQAdmin.class})
public class ProducerLoader implements ApplicationContextAware, InitializingBean, DisposableBean {


    private ConfigurableApplicationContext applicationContext;

    /**
     * rocket mq 集群地址ip+port;ip+port
     */
    @Value("${rocket.mq.address}")
    private String ADDRESS;

    public static Map<String, DefaultMQProducer> producerMap = new HashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            Map<String, BaseProducer> producerMap = applicationContext.getBeansOfType(BaseProducer.class)
                    .entrySet().stream().filter(entry -> {
                        return !ScopedProxyUtils.isScopedTarget(entry.getKey());
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (producerMap.isEmpty()) {
                return;
            }
            if (StringUtils.isBlank(ADDRESS)) {
                throw new NullPointerException("rocket.mq.address not configured");
            }
            log.info(">> 正在初始化rocket mq producer ......");
            if (StringUtils.isBlank(ADDRESS)) {
                throw new NullPointerException("rocket.mq.address not configured");
            }
            for (Map.Entry<String, BaseProducer> entry : producerMap.entrySet()) {
                initProducer(entry.getKey(), entry.getValue());
            }
            log.info(">> 初始化rocket mq producer 完毕,producer size:[" + producerMap.size() + "]");
        } catch (Exception e) {
            throw new RuntimeException("初始化rocket mq producer 监听异常[" + e.getMessage() + "]");
        }
    }

    /**
     * 根据不同的组初始化生产者
     *
     * @param beanName bean名成
     * @param bean     类对象
     *
     * @throws Exception 初始化生产者异常
     */
    private void initProducer(String beanName, BaseProducer bean) throws Exception {
        String groupName = bean.groupName();
        int retryCount = bean.retryCount() == null ? 2 : bean.retryCount();
        if (StringUtils.isBlank(groupName)) {
            throw new NullPointerException("groupName is null at " + bean.getClass().getName());
        }
        log.info(">> rocket mq address:[" + ADDRESS + "],beanName:[" + beanName + "],producer group:[" + groupName + "],send retry count[" + retryCount + "]");
        //todo 其他配置后续完善
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(ADDRESS);
        producer.setRetryTimesWhenSendAsyncFailed(retryCount);
        producer.start();
        producerMap.put(groupName, producer);
    }


    @Override
    public void destroy() throws Exception {
        for (Map.Entry<String, DefaultMQProducer> entry : producerMap.entrySet()) {
            DefaultMQProducer producer = entry.getValue();
            producer.shutdown();
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
