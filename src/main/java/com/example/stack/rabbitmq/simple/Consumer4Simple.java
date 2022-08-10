package com.example.stack.rabbitmq.simple;

import com.example.stack.rabbitmq.ConnectionUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author：yangtao
 * @create: 2022-08-09 10:24
 */

@Slf4j
public class Consumer4Simple extends DefaultConsumer{

    private final static String QUEUE_NAME = "module-simple";

    public Consumer4Simple(Channel channel) {
        super(channel);
    }
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        //接受从队列中发送的消息
        System.out.println(consumerTag);
        System.out.println("----------------收到消息了---------------");
        System.out.println("消息属性为：" + properties);
        System.out.println("消息内容为：" + new String(body));
        long deliveryTag = envelope.getDeliveryTag();
        System.out.println("deliveryTag: "+ deliveryTag);
        this.getChannel().basicAck(deliveryTag, false);
    }

    //如果是手工确认消息，需要在handleDelivery方法中进行相关的确认，代码如下：
    //手动确认
    //long deliveryTag = envelope.getDeliveryTag();
    //channel.basicAck(deliveryTag, false);

    public static void main(String[] args) throws Exception {
        //1、获取连接
        Connection connection = ConnectionUtil.getConnection("127.0.0.1",5672,"/","guest","guest");
        //2、声明信道
        Channel channel = connection.createChannel();
        //3、声明(创建)队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //监听队列中的消息
        channel.basicConsume(QUEUE_NAME,false,new Consumer4Simple(channel));

        TimeUnit.SECONDS.sleep(10);

        channel.close();
        connection.close();
    }























        /*
           true:表示自动确认，只要消息从队列中获取，无论消费者获取到消息后是否成功消费，都会认为消息已经成功消费

           false:表示手动确认，消费者获取消息后，服务器会将该消息标记为不可用状态，等待消费者的反馈，
                 如果消费者一直没有反馈，那么该消息将一直处于不可用状态，并且服务器会认为该消费者已经挂掉，不会再给其
                 发送消息，直到该消费者反馈。

            queue : 队列名称
            autoAck ： 设置是否自动确认，建义设置成false，即不自动确认
            consumerTag ： 消费者标签，用来区分多个消费者
            noLocal: 设置为true ,则表示不能将同一个Connection中生产者发送的消息传送给这个Connection中的消费者；
            exclusive:设置是否排他
            arguments : 设置消费者其他参数
            callback，设置消费者的回调函数，用来处理RabbitMQ推送过来的消息，比如DefaultConsumer，使用时需要客户端重要（override）其中的方法。
          */

        //上面的代码显示的设置了autoAck为false,然后接收的消息之后进行显示ack操作（channel.basicAck）
        //对于消费者来说，这个设置是非常必要的，可以防止消息不必要的丢失。
}
