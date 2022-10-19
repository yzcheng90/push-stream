package com.suke.axsc.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 *
 * 在nvr 的配置界面 -> 视音频 -> 配置所有摄像头 都为H264
 * H265 解码不了
 * https://cloud.tencent.com/developer/article/2036458
 **/
@Slf4j
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        log.info("推流服务启动成功：http://localhost:8055/index.html");
    }

}
