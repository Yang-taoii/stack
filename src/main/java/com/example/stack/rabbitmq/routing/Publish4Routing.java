package com.example.stack.rabbitmq.routing;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * @author：yangtao
 * @create: 2022-09-02 09:33
 */

public class Publish4Routing {


    private final static String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String message = "hello rabbitmq 指定路由key为select";

        //发布消息 指定路由key为update
        channel.basicPublish(EXCHANGE_NAME, "select", null, message.getBytes());

        System.out.println("生产者发送" + message + "'");
        channel.close();
        connection.close();
    }






}
