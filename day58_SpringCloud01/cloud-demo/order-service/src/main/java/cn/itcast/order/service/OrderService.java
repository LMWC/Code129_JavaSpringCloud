package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import cn.itcast.order.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RestTemplate restTemplate;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单


        Order order = orderMapper.findById(orderId);
        //实现 远程调用 从用户系统中获取到用户的ID 对应的信息  1 。需要创建restTemplate (就是模拟浏览器发送请求的组件)  2.直接使用它的方法实现调用动作
        //参数1 指定要发送的请求的路径
        //参数2 指定将来得到的JSON数据 会自动转换成对象的字节码对象类型
        //User user = restTemplate.getForObject("http://localhost:8081/user/" + order.getUserId(), User.class);
        User user = restTemplate.getForObject("http://userservice/user/" + order.getUserId(), User.class);

        // todo  使用动态从 eurekaserver中获取 服务名 对应的所有的ip和端口的列表  然后利用负载均衡策略 选择一台服务器 实现远程调用

        order.setUser(user);
        // 4.返回
        return order;
    }
}
