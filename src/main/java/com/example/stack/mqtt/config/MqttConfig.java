package com.example.stack.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author：yangtao
 * @create: 2022-08-29 17:41
 */
@Configuration
public class MqttConfig {

    /**
     * 默认订阅主题
     */
    public static final String DEFAULT_TOPIC = "mqtt/face/#";


    /**
     * 创建MqttPahoClientFactory，设置MQTT Broker连接属性，如果使用SSL验证，也在这里设置。
     * @return factory
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // 设置代理端的URL地址，可以是多个
        options.setServerURIs(new String[]{"tcp://47.97.114.254:1883"});
        //设置账号密码admin / public
        options.setUserName("admin");
        options.setPassword("public".toCharArray());

        factory.setConnectionOptions(options);

        return factory;
    }

    // 消费消息

    /**
     * 入站通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * 入站
     */
    @Bean
    public MessageProducer inbound() {
        // Paho客户端消息驱动通道适配器，主要用来订阅主题
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("consumerClient-server-get",
                mqttClientFactory(), DEFAULT_TOPIC);
        adapter.setCompletionTimeout(5000);

        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();
        // 按字节接收消息
//        defaultPahoMessageConverter.setPayloadAsBytes(true);
        adapter.setConverter(defaultPahoMessageConverter);
        // 设置QoS
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    /**
     * ServiceActivator注解 表明当前方法用于处理MQTT消息
     * inputChannel参数 指定了用于消费消息的channel
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String payload = message.getPayload().toString();

            // byte[] bytes = (byte[]) message.getPayload(); // 收到的消息是字节格式
            String topic = message.getHeaders().get("mqtt_receivedTopic").toString();

            /**
             * 匹配消息正则表达式  mqtt/face/数字/执行的操作
             */
            String patter = "^mqtt/face/\\d+/\\w+";

            // 根据主题分别进行消息处理。
            // 匹配：mqtt/face/#
            Pattern pattern = Pattern.compile(patter);
            Matcher matcher = pattern.matcher(topic);
            if (matcher.matches()) {
                // 获取操作
                String operation = topic.substring(topic.lastIndexOf("/") + 1);
                System.out.println("主题[" + topic  + "]: 接受到的消息：\r\n" + payload);
            } else {
                System.out.println("丢弃消息：主题[" + topic  + "], 负载的消息为：\r\n" + payload);
            }

        };
    }

    // 发送消息

    /**
     * 出站通道
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 出站
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler outbound() {

        // 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("consumerClient-server-send", mqttClientFactory());
        // 如果设置成true，即异步，发送消息时将不会阻塞。
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(DEFAULT_TOPIC);
        // 设置默认QoS
        messageHandler.setDefaultQos(1);

        // Paho消息转换器
        DefaultPahoMessageConverter defaultPahoMessageConverter = new DefaultPahoMessageConverter();

        // defaultPahoMessageConverter.setPayloadAsBytes(true); // 发送默认按字节类型发送消息
        messageHandler.setConverter(defaultPahoMessageConverter);
        return messageHandler;
    }
}
