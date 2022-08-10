
_RabbitMQ 的五种工作模式_：
    简单队列 work模式 发布/订阅模式 路由模式 主题模式
    1. simple 简单队列
    2. work 一个生产者对应多个消费者，但是只能有一个消费者获得消息！！！竞争消费者模式。

_四种交换器类型_：
    RabbitMQ常用的Exchange Type有fanout、direct、topic、headers这四种
    这四种类的exchange分别有以下一些属性，分别是：
        name：名称
        Durability：持久化标志，如果为true，则表明此exchange是持久化的
        Auto-delete：删除标志，当所有队列在完成使用此exchange时，是否删除





