package cn.itcast.feign.clients;

import cn.itcast.feign.pojo.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j

public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {

            //这个方法 就是降级的业务逻辑的处理 一旦远程调用失败 会自动的调用这个方法 返回默认的处理的逻辑（默认的数据，默认的头像，）
            @Override
            public User findById(Long id) {
                log.error("错误信息：",cause);
                User user = new User();
                user.setUsername("默认的张胜男");
                user.setId(10L);
                user.setAddress("默认的地址中粮");
                return user;
            }
        };
    }
}
