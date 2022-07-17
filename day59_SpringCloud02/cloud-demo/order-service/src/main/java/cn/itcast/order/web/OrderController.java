package cn.itcast.order.web;

import cn.itcast.order.pojo.Order;

import cn.itcast.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

   @Autowired
   private OrderService orderService;

    /**
     * 后台 发送请求 要获取订单的数据 并且 吧对应的用户的信息一起带过来返回给前端
     * @param orderId
     * @return
     */
    @GetMapping("{orderId}")
    public Order queryOrderByUserId(
            @PathVariable("orderId") Long orderId,
            @RequestHeader(
                    name = "Truth",  // 设置请求头的头名
                    required = false // 是否必须 这个请求头没有值 也不会报错，默认是true 如果没有这个请求头 就报错
            )
                    String headerValue
    ) {
        System.out.println(headerValue);
        // 根据id查询订单并返回
        return orderService.queryOrderById(orderId);
    }


}
