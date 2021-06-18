package com.distributed.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Ray
 * @date created in 2021/6/17 23:51
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DelegatingFilterProxy extends GenericFilter {

    private final List<GatewayFilter> filters;
    private static final String HEALTH_URL = "/health";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String path = servletRequest.getServletPath();

        // 放行心跳检查
        if (HEALTH_URL.equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        FilterChainProxy gatewayFilterChain = new FilterChainProxy(filters);
        if (log.isDebugEnabled()) {
            List<String> filterNames = gatewayFilterChain.getFilters().stream()
                    .map(gatewayFilter -> gatewayFilter.getClass().getSimpleName())
                    .collect(Collectors.toList());
            log.debug("filter: {}", filterNames);
        }

        try {
            // 传给 GatewayFilter Chain
            gatewayFilterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error("have a error：{}", e.getMessage());
            e.printStackTrace();
        }

    }

    @RequiredArgsConstructor
    @Getter
    static class FilterChainProxy implements GatewayFilterChain {

        private final List<GatewayFilter> filters;
        private final AtomicInteger filterCount = new AtomicInteger();

        @Override
        public void doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
            int count = filterCount.getAndIncrement();
            if (count == filters.size()) {
                return;
            }

            filters.get(count).doFilter(request, response, this);
        }
    }
}
