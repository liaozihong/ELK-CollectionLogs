package com.hh.test.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LogsApplication {

    private final static Logger logger = LoggerFactory.getLogger(LogsApplication.class);

    private final String LoggerTemplate = "code:{},msg:{},param:{}";

    @RequestMapping("/")
    public String test() {
        logger.info("测试录入普通日志");
        logger.warn("测试录入警告日志");
        try {
            int i = 5 / 0;
        } catch (Exception e) {
            logger.error(LoggerTemplate, 200, "测试录入错误日志", e);
        }
        return "success";
    }

    public static void main(String[] args) {
        SpringApplication.run(LogsApplication.class, args);
    }
}
