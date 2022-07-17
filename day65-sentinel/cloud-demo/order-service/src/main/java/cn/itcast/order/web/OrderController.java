package cn.itcast.order.web;

import cn.itcast.order.pojo.Order;
import cn.itcast.order.service.OrderService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    @SentinelResource("hot")
    public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
        // 根据id查询订单并返回
        return orderService.queryOrderById(orderId);
//        System.out.println("=====================执行了===========");
//        return null;
    }

    //当 更新 的请求 超过了阈值 ，对 query 进行限流

    //不那么重要 订单
   /* @GetMapping("/query")
    public String queryOrder() {
        return "查询订单成功";
    }

    //比较重要 支付
    @GetMapping("/update")
    public String updateOrder() {
        return "更新订单成功";
    }*/




    //查询订单
    @GetMapping("/query")
    public String queryOrder() {
        // 查询商品
        orderService.queryGoods();
        // 查询订单
        System.out.println("查询订单");
        return "查询订单成功";
    }



    //创建订单
    @GetMapping("/save")
    public String saveOrder() {
        // 查询商品
        orderService.queryGoods();
        // 新增订单
        System.err.println("新增订单");
        return "新增订单成功";
    }

}
