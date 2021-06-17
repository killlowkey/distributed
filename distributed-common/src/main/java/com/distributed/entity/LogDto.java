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
public class LogDto {
    private String serviceName;
    private String content;
}
