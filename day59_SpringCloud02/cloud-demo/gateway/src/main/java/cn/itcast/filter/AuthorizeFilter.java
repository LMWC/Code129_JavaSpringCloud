package cn.itcast.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//推荐使用接口的方式
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //逻辑： 拦截请求 获取到请求参数 并获取参数名叫做：authorization 获取里面的内容 判断是否等于 admin 如果是就OK 放行，否则就报错(响应)


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取请求对象和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.获取当前的请求的路径 获取到路径中的参数 及值
        String authorization = request.getQueryParams().getFirst("authorization");
        if ("admin".equals(authorization)) {
            //3.判断是否等于admin 如果是 则放行 否则报错
            return chain.filter(exchange);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //放回响应
        return response.setComplete();
    }

    //排序的值  值越低  过滤器优先级越高
    @Override
    public int getOrder() {
        return -1;
    }
}
