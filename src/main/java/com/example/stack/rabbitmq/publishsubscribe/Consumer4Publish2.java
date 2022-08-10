package com.example.stack.rabbitmq.publishsubscribe;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.example.stack.rabbitmq.work.Consumer4Work1;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author：yangtao
 * @create: 2022-08-09 15:06
 */
public class Consumer4Publish2 extends DefaultConsumer {

    private final static String QUEUE_NAME = "fanout_queue_2";

    private final static String EXCHANGE_NAME = "fanout_exchange";

    public Consumer4Publish2(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //接受从队列中发送的消息
        System.out.println("---------Consumer4Publish2-------收到消息了---------------");
        System.out.println("消息属性为：" + properties);
        System.out.println("消息内容为：" + new String(body));
        long deliveryTag = envelope.getDeliveryTag();
        this.getChannel().basicAck(deliveryTag, false);
    }

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
        //同一时刻服务器只会发送一条消息给消费者 效率高的消费者消费消息多。可以用来进行负载均衡。
        channel.basicQos(1);
        channel.basicConsume(QUEUE_NAME,false,new Consumer4Work1(channel));
    }
}
