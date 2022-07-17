package cn.itcast.feign.clients;


import cn.itcast.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//兜底 回滚 备用 备胎
@FeignClient(value = "userservice")
public interface UserClient {

    //编写一个降级的逻辑（默认的值 一旦feign远程调用失败 则使用默认的值返回）

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);
}
