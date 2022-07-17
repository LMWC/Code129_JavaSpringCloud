package cn.itcast.feign.config;

import cn.itcast.feign.clients.UserClientFallback;
import cn.itcast.feign.clients.UserClientFallbackFactory;
import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }

    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }

    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }
}
