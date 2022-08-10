package com.example.stack.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * 消息队列
 * @author yangtao
 */
@Component
public class ConnectionUtil {

    public static Connection getConnection(String host, int port, String vHost, String userName, String passWord) throws Exception{
           //1、定义连接工厂
           ConnectionFactory factory = new ConnectionFactory();
           //2、设置服务器地址
           factory.setHost(host);
           //3、设置端口
           factory.setPort(port);
           //4、设置虚拟主机、用户名、密码
           factory.setVirtualHost(vHost);
           factory.setUsername(userName);
           factory.setPassword(passWord);
           //5、通过连接工厂获取连接
        return factory.newConnection();
       }




}
