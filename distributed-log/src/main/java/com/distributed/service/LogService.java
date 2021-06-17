package com.distributed.service;

import com.distributed.entity.LogDto;

/**
 * @author Ray
 */
public interface LogService {
    /**
     * 保存日志
     *
     * @param logDto 日志 dto
     */
    void writeLog(LogDto logDto);
}
