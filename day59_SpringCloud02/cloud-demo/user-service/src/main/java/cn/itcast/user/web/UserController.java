package cn.itcast.user.web;

import cn.itcast.user.pojo.MyJdbcProperties;
import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RefreshScope//刷新配置 （当nacos的配置中心修改了配置文件的之后 会立刻刷新spring里面的内容）
public class UserController {

    @Autowired
    private UserService userService;

//    @Value("${jdbc.url}")
//    private String url;
//
//    @Value("${jdbc.host}")
//    private String host;

    @Autowired
    private MyJdbcProperties properties;




    /**
     * 路径： /user/110
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id,
                          @RequestHeader(
                                  name = "Truth",  // 设置请求头的头名
                                  required = false // 是否必须 这个请求头没有值 也不会报错，默认是true 如果没有这个请求头 就报错
                          )
                                  String headerValue
    ) {
        //获取请求头
        System.out.println("头值："+headerValue);


        return userService.queryById(id);
    }

    @GetMapping("/info")
    public String getInfo(){

        System.out.println(properties.getUrl()+">>>>>>>>>>>>>>>>>>>>>>>>"+properties.getUsername());
        return "ok";
//        return url+">>>>"+host;
    }
}
