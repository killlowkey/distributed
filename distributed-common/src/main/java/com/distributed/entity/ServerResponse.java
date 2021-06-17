package com.distributed.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ray
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResponse<T> {

    static int SUCCESS_CODE = 200;
    static int ERROR_CODE = 500;

    private int code;
    private String msg;
    private T data;

    public static <T> ServerResponse<T> success(String msg) {
        return success(msg, null);
    }

    public static <T> ServerResponse<T> success(T data) {
        return success("success", data);
    }

    public static <T> ServerResponse<T> success(String msg, T data) {
        return new ServerResponse<>(SUCCESS_CODE, msg, data);
    }

    public static <T> ServerResponse<T> error(String msg) {
        return error(msg, null);
    }

    public static <T> ServerResponse<T> error(String msg, T data) {
        return new ServerResponse<>(ERROR_CODE, msg, data);
    }
}
