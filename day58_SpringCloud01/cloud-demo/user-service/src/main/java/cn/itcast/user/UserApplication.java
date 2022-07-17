package cn.itcast.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("cn.itcast.user.mapper")
@SpringBootApplication
//@EnableEurekaClient//开启客户端的服务的注册与发现功能 这个注解可以不用写，默认就是有的
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
