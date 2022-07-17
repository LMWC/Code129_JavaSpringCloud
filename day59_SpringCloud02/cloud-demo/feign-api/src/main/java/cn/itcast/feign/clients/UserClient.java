package cn.itcast.feign.clients;

import cn.itcast.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//configuration的配置就是局部生效
@FeignClient(name = "userservice")//name指定要向谁 发送请求 谁（指定的是服务名称:spring.application.name的值）
public interface UserClient {
    //声明： 我要向用户微服务发送请求 请求是啥呢: GET 路径：/user/{id} 返回值：User     根据用户ID获取用户对象信息（user.class类型）
    //底层逻辑： 模拟浏览器发送请求
    @GetMapping("/user/{id}")
    User findById(@PathVariable(name = "id") Long id);


    /**
     *  orderservice  调用 userservice
     * 编写接口的要求：
     *  1.方法的返回值 一定要和 被调用方的controller的方法的返回值一样
     *  2.方法的参数个数和类型 也要和被调用方的controller的方法的参数个数和类型一致
     *  3.方法上的请求的路径 也要和被调用方的controller的方法的请求路径一致
     *  4.参数的注解也需要加上 （可以不加） 墙裂建议 注解给写上
     */
}
