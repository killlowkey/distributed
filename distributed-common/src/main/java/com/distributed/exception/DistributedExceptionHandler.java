package com.distributed.exception;

import com.distributed.entity.ServerResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ray
 */
@ControllerAdvice
public class DistributedExceptionHandler {

    @ExceptionHandler(DistributedException.class)
    @ResponseBody
    public <T> ServerResponse<T> handler(DistributedException ex) {
        return ServerResponse.error(ex.getMsg());
    }
}
