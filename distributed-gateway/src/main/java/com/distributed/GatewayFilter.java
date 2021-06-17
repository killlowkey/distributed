package com.distributed;

import com.distributed.entity.ServerResponse;
import com.distributed.exception.DistributedException;
import com.distributed.registry.ServiceDiscovery;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
public class GatewayFilter extends GenericFilter {

    private final ServiceDiscovery serviceDiscovery;
    private final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();
    private static final String HEALTH_URL = "/health";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String path = servletRequest.getServletPath();

        // 放行心跳请求
        if (!HEALTH_URL.equals(path)) {
            try {
                route(servletRequest, servletResponse);
            } catch (DistributedException ex) {
                // 未找到服务
                log.error(ex.getMsg());
                byte[] data = gson.toJson(ServerResponse.error(ex.getMsg())).getBytes();
                writeData(servletResponse, data);
            }
            return;
        }

        chain.doFilter(request, response);
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
        String serviceUrl = serviceDiscovery.getService(serviceName);

        String targetUrl = serviceUrl + path;
        Request okRequest = new Request.Builder()
                .url(targetUrl)
                .method(method, RequestBody.create(com.squareup.okhttp.MediaType.parse("application/json"),
                        Objects.requireNonNull(readBody(request))))
                .build();
        try {
            // 请求服务
            Response serviceResponse = client.newCall(okRequest).execute();
            log.info("route {} url success", targetUrl);
            // 写回数据
            writeData(response, serviceResponse.body().bytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(HttpServletResponse response, byte[] data) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] readBody(HttpServletRequest request) {
        try (
                ServletInputStream inputStream = request.getInputStream();
        ) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamUtils.copy(inputStream, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }
}
