package com.distributed.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ray
 * @date created in 2021/6/17 23:41
 */
public interface GatewayFilter {

    void doFilter(HttpServletRequest request, HttpServletResponse response,
                  GatewayFilterChain chain) throws Exception;

}
