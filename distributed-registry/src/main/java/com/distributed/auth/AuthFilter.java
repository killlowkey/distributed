package com.distributed.auth;

import com.distributed.entity.ServerResponse;
import com.distributed.util.WebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ray
 * @date created in 2021/6/22 23:48
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends GenericFilter {

    private final AuthHolder authHolder;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String path = servletRequest.getServletPath();
        String method = servletRequest.getMethod();

        // 放行注册服务请求
        if ("/services".equals(path) && method.equals(HttpMethod.POST.name())) {
            chain.doFilter(request, response);
            return;
        }

        String token = servletRequest.getHeader("token");
        if (!StringUtils.hasText(token)) {
            WebUtils.writeBody(servletResponse, ServerResponse.error("not found token"));
            return;
        }

        String url = servletRequest.getHeader("url");
        if (!StringUtils.hasText(url)) {
            WebUtils.writeBody(servletResponse, ServerResponse.error("not found url header"));
            return;
        }

        // 认证 token
        if (!authHolder.authToken(url, token)) {
            WebUtils.writeBody(servletResponse, ServerResponse.error("token error"));
            return;
        }

        chain.doFilter(request, response);
    }
}
