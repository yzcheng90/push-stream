package com.suke.axsc.web.config;

import com.suke.axsc.common.push.cmd.CommandManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author czx
 * @title: ServiceStopListener
 * @projectName push-stream
 * @description: TODO 服务停止监听
 * @date 2022/9/309:26
 */
@Slf4j
@Component
public class ServiceStopListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    public CommandManager commandManager;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("服务即将停止...");
        commandManager.stopAll();
        log.info("销毁后台资源和保活线程！");
        commandManager.destory();
    }
}
