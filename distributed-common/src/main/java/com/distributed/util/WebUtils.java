package com.distributed.util;

import com.distributed.entity.ServerResponse;
import com.google.gson.Gson;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Response;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Ray
 * @date created in 2021/6/18 0:14
 */
public class WebUtils {

    // token 存放的请求头
    public static final String JWT_HEADER = "Authorization";
    //  Basic token
    public static final String START_WITH = "Basic";
    private static final Gson GSON = new Gson();

    private WebUtils() {

    }

    public static void writeBody(HttpServletResponse response, byte[] body) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeResponse(HttpServletResponse response, Response serviceResponse) {
        try (
                ServletOutputStream outputStream = response.getOutputStream();
        ) {
            // 写入 header
            Headers headers = serviceResponse.headers();
            headers.names().forEach(headerName -> response.addHeader(headerName, headers.get(headerName)));

            // 写入 body
            outputStream.write(serviceResponse.body().bytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void writeBody(HttpServletResponse response, ServerResponse<T> data) {
        writeBody(response, GSON.toJson(data).getBytes());
    }

    public static byte[] readBody(HttpServletRequest request) {
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

    public static String getAuthToken(HttpServletRequest request) {
        String header = request.getHeader(JWT_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(START_WITH)) {
            return "";
        }

        return header.split(" ")[1];
    }
}
