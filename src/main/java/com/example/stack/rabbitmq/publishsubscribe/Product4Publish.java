package com.example.stack.rabbitmq.publishsubscribe;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author：yangtao
 * @create: 2022-08-09 15:00
 */
public class Product4Publish {

    private final static String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws Exception {
        //1、获取连接
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        //2、声明信道
        Channel channel = connection.createChannel();
        //3、声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //4、定义消息内容(发布多条消息)
        for (int i = 0; i < 20; i++) {
            String message = "hello rabbitmq " + i;
            //5、发布消息
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println("[x] Sent'" + message + "'");
            //模拟发送消息延时，便于演示多个消费者竞争接受消息
            Thread.sleep(i * 10);
        }
        //6、关闭通道
        channel.close();
        //7、关闭连接
        connection.close();
    }


}
