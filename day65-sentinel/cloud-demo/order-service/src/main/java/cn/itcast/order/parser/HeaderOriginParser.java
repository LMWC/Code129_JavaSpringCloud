package cn.itcast.order.parser;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
public class HeaderOriginParser implements RequestOriginParser {

    //这个返回值 就是 请求头中的值（就是需要配置到授权规则的流控的应用 名称）
    @Override
    public String parseOrigin(HttpServletRequest request) {
        String origin = request.getHeader("origin");
        if(StringUtils.isEmpty(origin)){
            origin="blank";//从浏览器来的请求 设置一个值
        }
        return origin;
    }
}
