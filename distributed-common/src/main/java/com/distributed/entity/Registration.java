package com.distributed.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Ray
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration implements Serializable {

    private static final long serialVersionUID = -5152274218618353997L;

    private String serviceName;
    private String serviceUrl;
}
