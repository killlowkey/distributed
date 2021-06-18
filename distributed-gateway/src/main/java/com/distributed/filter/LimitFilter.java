package com.distributed.filter;

import com.distributed.exception.DistributedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接口限流
 *
 * @author Ray
 * @date created in 2021/6/18 8:19
 */
@Component
@Slf4j
@Order(200)
public class LimitFilter implements GatewayFilter {

    @Value("${limit.count}")
    private int count;

    @Value("${limit.second}")
    private int second;

    private Date date = new Date();

    private final Map<String, AtomicInteger> limitData = new ConcurrentHashMap<>();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         GatewayFilterChain chain) throws Exception {

        if (log.isTraceEnabled()) {
            log.trace("LimitFilter process");
        }

        // 每隔 second 秒清理限流数据
        if ((System.currentTimeMillis() - date.getTime()) > second * 1000L) {
            limitData.clear();
            date = new Date();
        }

        // 接口限流处理
        String requestId = generateRequestId(request);
        AtomicInteger atomicInteger = limitData.putIfAbsent(requestId, new AtomicInteger(1));
        int currentCount = atomicInteger == null ? 1 : atomicInteger.incrementAndGet();
        if (currentCount > count) {
            log.info("{} Triggered interface limit", requestId);
            throw new DistributedException("触发限流接口");
        }

        chain.doFilter(request, response);
    }

    private String generateRequestId(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        return method + "@" + path;
    }
}
