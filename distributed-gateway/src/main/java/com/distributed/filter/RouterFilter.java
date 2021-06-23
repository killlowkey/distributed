package com.distributed.filter;

import com.distributed.exception.DistributedException;
import com.distributed.registry.ServiceDiscovery;
import com.distributed.util.WebUtils;
import com.squareup.okhttp.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.Objects;

/**
 * 网关路由
 *
 * @author Ray
 * @date created in 2021/6/17 20:14
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class RouterFilter implements GatewayFilter {

    private final ServiceDiscovery serviceDiscovery;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         GatewayFilterChain chain) throws Exception {

        if (log.isTraceEnabled()) {
            log.trace("RoutingFilter process");
        }

        route(request, response);
    }

    /**
     * 路由转发
     *
     * @param request  客户端请求
     * @param response 客户端响应
     */
    private void route(HttpServletRequest request, HttpServletResponse response) {
        String method = request.getMethod();
        String path = request.getServletPath();
        String temp = path.split("/")[1];
        String serviceName = temp.substring(0, 1).toUpperCase() + temp.substring(1) + "-Service";
        // 获取服务的 url
        String serviceUrl = serviceDiscovery.getServiceUrl(serviceName);

        String targetUrl = serviceUrl + path;
        Request okRequest = new Request.Builder()
                .url(targetUrl)
                // 添加请求的 header
                .headers(toOkHttpHeaders(request))
                .method(method, RequestBody.create(com.squareup.okhttp.MediaType.parse("application/json"),
                        Objects.requireNonNull(WebUtils.readBody(request))))
                .build();
        try {
            // 请求服务
            Response serviceResponse = client.newCall(okRequest).execute();
            log.info("route {} url success", targetUrl);
            WebUtils.writeResponse(response, serviceResponse);
        } catch (IOException e) {
            if (e instanceof ConnectException) {
                throw new DistributedException(String.format("%s service died", serviceName));
            }

            throw new DistributedException(e.getMessage());
        }
    }

    private Headers toOkHttpHeaders(HttpServletRequest request) {
        Headers.Builder builder = new Headers.Builder();
        Iterator<String> iterator = request.getHeaderNames().asIterator();
        iterator.forEachRemaining(name -> builder.add(name, request.getHeader(name)));
        return builder.build();
    }
}
