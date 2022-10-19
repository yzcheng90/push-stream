package com.suke.axsc.common.push.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author czx
 * @title: PushConfig
 * @projectName push-stream
 * @description: TODO
 * @date 2022/9/2911:20
 */
@Data
@ConfigurationProperties(prefix = "push-stream")
public class PushConfig {

    /**
     * ffmpeg路径
     **/
    public String ffmpegPath;
    /**
     * 任务数量
     **/
    public Integer taskSize;
    /**
     *开启保活线程
     **/
    public boolean keepalive;
    /**
     * 是否输出debug消息
     **/
    public boolean debug;
}
