# 工程简介

rocket mq 组件封装

# 延伸阅读

使用步骤： 1、将pom.xml引入配置

```
<dependency>
<groupId>com.liuxn.ambi</groupId>
<artifactId>ambi-mgt-rocketmq</artifactId>
<version>1.0-SNAPSHOT</version>
</dependency>

```

2、编写消费者监听实现IConsumerManager接口即可.多个消费多次实现肌即可 demo

```
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
    

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
       //这里写自己的业务
       return null; 
  }
 }
```

3、生产者 初始化 集成 ProducerManager 抽象类

```
@Component
public class Test1Producer extends ProducerManager {

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
//调用：
    @Autowired
    private TestProducer testProducer;
    testProducer.send();

```

4、配置文件

```
rocket mq 服务地址
rocket:
  mq:
    address: 192.168.200.214:9876
 
```

5、group topic tag [使用规范]<https://help.aliyun.com/document_detail/43523.html?spm=a2c4g.11186623.2.15.21d11da9tr3NbG>

6、引入jar后。 Springboot 启动时自动扫描并初始rocketmq链接



