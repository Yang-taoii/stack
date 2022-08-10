package com.example.stack.rabbitmq.simple;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author：yangtao
 * @create: 2022-08-09 10:24
 */
public class Product4Simple {

    private final static String QUEUE_NAME = "module-simple";

    public static void main(String[] args) throws Exception{
        //1、获取连接
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        //2、声明信道
        Channel channel = connection.createChannel();
        //3、声明(创建)队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //4、定义消息内容
        String message = "hello rabbitmq ";
        //5、发布消息
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("[x] Sent'"+message+"'");
        //6、关闭通道
        channel.close();
        //7、关闭连接
        connection.close();
    }
}
