package cn.itcast.feign.clients;

import cn.itcast.feign.pojo.User;

public class UserClientFallback implements UserClient {

    //实现类的方法就是默认的降级的逻辑处理
    @Override
    public User findById(Long id) {
        User user = new User();
        user.setUsername("默认的张胜男fallback");
        user.setId(10L);
        user.setAddress("默认的地址中粮fallback");
        return user;
    }
}
