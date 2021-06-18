package com.distributed.filter;

import com.distributed.exception.DistributedException;
import com.distributed.util.JwtUtils;
import com.distributed.util.WebUtils;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证过滤器
 *
 * @author Ray
 * @date created in 2021/6/18 0:02
 */
@Component
@Slf4j
@Order(100)
public class AuthenticationFilter implements GatewayFilter {

    private static final String LOGIN_URL = "/auth/login";

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         GatewayFilterChain chain) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("AuthenticationFilter process");
        }

        // 放行登录请求
        String path = request.getServletPath();
        String method = request.getMethod();
        if (LOGIN_URL.equals(path) && HttpMethod.POST.matches(method)) {
            chain.doFilter(request, response);
        }

        String token = WebUtils.getAuthToken(request);
        if (!StringUtils.hasText(token)) {
            throw new DistributedException("用户未登录");
        }

        try {
            if (JwtUtils.verifyToken(token)) {
                chain.doFilter(request, response);
            }
        } catch (JwtException ex) {
            throw new DistributedException(ex.getMessage());
        }

    }

}
