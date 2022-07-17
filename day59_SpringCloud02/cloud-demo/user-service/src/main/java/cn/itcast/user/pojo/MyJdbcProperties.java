package cn.itcast.user.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jdbc")
public class MyJdbcProperties {
    private String url;
    private String username;
    private String password;
}
