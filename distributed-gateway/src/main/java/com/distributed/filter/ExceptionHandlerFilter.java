package com.distributed.filter;

import com.distributed.entity.ServerResponse;
import com.distributed.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常处理 filter
 *
 * @author Ray
 * @date created in 2021/6/18 10:05
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerFilter implements GatewayFilter {
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, GatewayFilterChain chain)
            throws Exception {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            WebUtils.writeBody(response, ServerResponse.error(ex.getMessage()));
        }
    }
}
