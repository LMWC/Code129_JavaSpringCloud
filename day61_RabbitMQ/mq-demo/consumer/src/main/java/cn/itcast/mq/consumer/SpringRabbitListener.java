package cn.itcast.mq.consumer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SpringRabbitListener {

    //从哪个队列去接受消息
    /*@RabbitListener(queues = "simple.queue")
    public void jieshouxiaoxi(String msg) {
        System.out.println("我接受到了消息" + msg);
    }*/

    //==============work queue

    /*@RabbitListener(queues = "simple.queue")
    public void consumer1(String msg) throws Exception {
        System.out.println("我接受到了消息" + msg);
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void consumer2(String msg)  throws Exception{
        System.err.println("我接受到了消息" + msg);
        Thread.sleep(100);
    }*/

    //两个消费者 分别监听 队列 1 和队列2  广播模式

    /*@RabbitListener(queues = "fanout.queue1")
    public void consumerFanout1(String msg) throws Exception {
        System.out.println("我接受到了消息" + msg);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void consumerFanout2(String msg)  throws Exception{
        System.out.println("我接受到了消息" + msg);
    }*/

    //====================== 路由模式 2个消费者 监听和绑定放到一起实现

    /*@RabbitListener(
            bindings = @QueueBinding(
                 value=@Queue(value="direct.queue1"),
                 exchange = @Exchange(value="itcast.direct",type = ExchangeTypes.DIRECT),
                 key={"blue","red"}
            )
    )
    public void consumerDirect1(String msg) throws Exception {
        System.out.println("我消费者1 接受到了消息" + msg);
    }



    @RabbitListener(
            bindings = @QueueBinding(
                    value=@Queue(value="direct.queue2"),
                    exchange = @Exchange(value="itcast.direct",type = ExchangeTypes.DIRECT),
                    key={"yellow","red"}
            )
    )
    public void consumerDirect2(String msg)  throws Exception{
        System.out.println("我是消费者2接受到了消息" + msg);
    }*/

    //====================== 主题模式 2个消费者 监听和绑定放到一起实现
    @RabbitListener(
            bindings = @QueueBinding(
                    value=@Queue(value="topic.queue1"),
                    exchange = @Exchange(value="itcast.topic",type = ExchangeTypes.TOPIC),
                    key={"china.#"}
            )
    )
    public void consumerDirect1(String msg) throws Exception {
        System.out.println("我消费者1 接受到了消息" + msg);
    }



    @RabbitListener(
            bindings = @QueueBinding(
                    value=@Queue(value="topic.queue2"),
                    exchange = @Exchange(value="itcast.topic",type = ExchangeTypes.TOPIC),
                    key={"#.news"}
            )
    )
    public void consumerDirect2(String msg)  throws Exception{
        System.out.println("我是消费者2接受到了消息" + msg);
    }

    //====================== 接收json
    @RabbitListener(queues = "object.queue")
    public void ObjectMsg(String msg)  throws Exception{
        System.err.println("我接受到了消息" + msg);
    }


}
