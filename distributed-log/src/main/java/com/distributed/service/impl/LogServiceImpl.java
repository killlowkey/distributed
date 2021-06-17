package com.distributed.service.impl;

import com.distributed.entity.LogDto;
import com.distributed.service.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ray
 */
@Service
public class LogServiceImpl implements LogService {

    @Value("${log.file.path}")
    private String logFilePath;

    private PrintWriter pw;

    @PostConstruct
    public void init() throws IOException {
        pw = new PrintWriter(new FileOutputStream(logFilePath, true));
    }


    @Override
    public void writeLog(LogDto logDto) {
        pw.write(String.format("%s：【%s】-%s\n", formatData(), logDto.getServiceName(), logDto.getContent()));
        pw.flush();
    }

    String formatData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}
