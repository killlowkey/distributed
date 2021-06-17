package com.distributed.controller;

import com.distributed.entity.LogDto;
import com.distributed.entity.ServerResponse;
import com.distributed.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ray
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {

    private final LogService logService;

    @PostMapping
    public <T> ServerResponse<T> createLog(@RequestBody LogDto logDto) {
        log.info(String.format("receive 【%s】service log：%s", logDto.getServiceName(), logDto.getContent()));
        logService.writeLog(logDto);
        return ServerResponse.success("保存日志成功");
    }
}
