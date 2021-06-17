package com.distributed.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ray
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DistributedException extends RuntimeException {

    private final String msg;

    public DistributedException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
