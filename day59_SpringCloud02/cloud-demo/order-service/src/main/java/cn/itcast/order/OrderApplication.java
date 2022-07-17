package cn.itcast.order;


import cn.itcast.feign.clients.UserClient;
import cn.itcast.feign.config.DefaultFeignConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@MapperScan("cn.itcast.order.mapper")
@SpringBootApplication
// defaultConfiguration = DefaultFeignConfiguration.class 是全局生效
@EnableFeignClients(basePackages = "cn.itcast.feign.clients",defaultConfiguration = DefaultFeignConfiguration.class)//开启声明式feign客户端（feign就用了）
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    //将组件交给spring容器管理
    @Bean
    @LoadBalanced//开启负载均衡 自动的实现负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /*@Bean
    public IRule iRule(){
        //随机的规则
        return new RandomRule();
    }*/



}