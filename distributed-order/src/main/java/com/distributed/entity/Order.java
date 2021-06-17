package com.distributed.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ray
 */
@Data
@AllArgsConstructor
public class Order {
    private int id;
    private String name;
    private float money;
}
