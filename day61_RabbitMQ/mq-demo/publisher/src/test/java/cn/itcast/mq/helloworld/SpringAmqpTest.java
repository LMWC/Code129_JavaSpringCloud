package cn.itcast.mq.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class SpringAmqpTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;//restTemplate redisTemplate jdbcTemplate


   /* @Test
    public void sendmessage() {
        //参数1 指定要发送消息到的交换机 这里我们使用默认的交换机 不写 空着
        //参数2 指定要发送消息到的 队列 队列指定
        //参数3 指定要发送消息的内容
        rabbitTemplate.convertAndSend("", "simple.queue", "hello rabbitmq");
    }*/

    //============================work queue模拟

    @Test
    public void sendmessage()  throws Exception{
        //参数1 指定要发送消息到的交换机 这里我们使用默认的交换机 不写 空着
        //参数2 指定要发送消息到的 队列 队列指定
        //参数3 指定要发送消息的内容

        String message = "hello, message__";
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend("", "simple.queue", message+i);
            Thread.sleep(20);
        }
    }

    //发送消息 广播模式
    @Test
    public void send2(){
        String message = "hello, fanout";
        //参数1 指定交换机的名称
        //参数2 指定routingkey 在广播模式下 这个值就是空值
        //参数2 指定消息的内容
        rabbitTemplate.convertAndSend("itcast.fanout","",message);
    }

    //路由模式
    @Test
    public void sendDirect(){
        String message1 = "hello, direct11111";
        String message2 = "hello, direct12222";
        String message3 = "hello, directall";
        //参数1 指定交换机的名称
        //参数2 指定routingkey 在路由模式下 必须要指定routingkey 将来交换机会根据这个值 匹配这个key的 转发到绑定了这个交换机的队列中
        //参数2 指定消息的内容
        rabbitTemplate.convertAndSend("itcast.direct","blue",message1);
        rabbitTemplate.convertAndSend("itcast.direct","yellow",message2);
        rabbitTemplate.convertAndSend("itcast.direct","red",message3);
    }



    //主题模式
    @Test
    public void sendTopic(){
        String message1 = "hello, china的信息";
        String message2 = "hello, 新闻信息";
        String message3 = "hello";
        //参数1 指定交换机的名称
        //参数2 指定routingkey 在路由模式下 必须要指定routingkey 将来交换机会根据这个值 匹配这个key的 转发到绑定了这个交换机的队列中
        //参数2 指定消息的内容
        rabbitTemplate.convertAndSend("itcast.topic","china.wusuowei",message1);
        rabbitTemplate.convertAndSend("itcast.topic","wusuowei.news",message2);
        rabbitTemplate.convertAndSend("itcast.topic","china.news",message3);
    }

    //内部实现了序列化 自动将对象 使用java默认的序列化机制进行序列化变成字节数组  可以修改 用自定义的转换器 实现自动转换变成 JSON格式存储。
    @Test
    public void sendObjectMsg(){
        Map<String,String> map = new HashMap<>();//object类型  --->json格式进行存储的话。
        map.put("name","柳岩女神");
        map.put("age","30");
        map.put("id","1");
        rabbitTemplate.convertAndSend("", "object.queue", map);
    }
}
