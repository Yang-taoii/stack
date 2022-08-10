package com.example.stack.rabbitmq.work;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.rabbitmq.client.*;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author：yangtao
 * @create: 2022-08-09 14:09
 */
public class Consumer4Work2 extends DefaultConsumer {

    private final static String QUEUE_NAME = "work_queue";

    public Consumer4Work2(Channel channel) {
        super(channel);
    }

    @SneakyThrows
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //接受从队列中发送的消息
        System.out.println("---------Consumer4Work2-------收到消息了---------------");
        System.out.println("消息属性为：" + properties);
        System.out.println("消息内容为：" + new String(body));
        long deliveryTag = envelope.getDeliveryTag();
        this.getChannel().basicAck(deliveryTag, false);
        TimeUnit.SECONDS.sleep(2);
    }

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //同一时刻服务器只会发送一条消息给消费者 效率高的消费者消费消息多。可以用来进行负载均衡。
        channel.basicQos(1);
        channel.basicConsume(QUEUE_NAME,false,new Consumer4Work2(channel));
//        TimeUnit.SECONDS.sleep(10);
//        channel.close();
//        connection.close();
    }



}
