package com.distributed.auth;

import com.distributed.annotation.ConditionalOnExclude;
import com.distributed.annotation.Services;
import com.distributed.entity.ServerResponse;
import com.distributed.util.JwtUtils;
import com.distributed.util.WebUtils;
import io.jsonwebtoken.JwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ray
 * @date created in 2021/6/18 11:24
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConditionalOnExclude({
        Services.REGISTRY, Services.GATEWAY
})
public class TokenFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String path = servletRequest.getServletPath();

        if ("/auth/login".equals(path) || "/health".equals(path)){
            chain.doFilter(request, response);
            return;
        }

        String authToken = WebUtils.getAuthToken(servletRequest);
        if (!StringUtils.hasText(authToken)) {
            WebUtils.writeBody((HttpServletResponse) response, ServerResponse.error("用户未登录"));
            return;
        }

        try {
            // 校验 token
            if (JwtUtils.verifyToken(authToken)) {
                chain.doFilter(request, response);
            }
        } catch (JwtException ex) {
            WebUtils.writeBody((HttpServletResponse) response, ServerResponse.error(ex.getMessage()));
        }
    }
}
