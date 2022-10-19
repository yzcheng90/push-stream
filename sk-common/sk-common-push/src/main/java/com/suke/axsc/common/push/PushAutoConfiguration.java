package com.suke.axsc.common.push;

import com.suke.axsc.common.push.cmd.CommandManager;
import com.suke.axsc.common.push.cmd.CommandManagerImpl;
import com.suke.axsc.common.push.util.ApplicationContextRegister;
import com.suke.axsc.common.push.config.PushConfig;
import com.suke.axsc.common.push.data.TaskDao;
import com.suke.axsc.common.push.data.TaskDaoImpl;
import com.suke.axsc.common.push.handler.KeepAliveHandler;
import com.suke.axsc.common.push.handler.ProcessWatchHandler;
import com.suke.axsc.common.push.handler.TaskHandler;
import com.suke.axsc.common.push.handler.TaskHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author czx
 * @title: PushAutoConfiguration
 * @projectName push-stream
 * @description: TODO
 * @date 2022/9/2911:47
 */
@EnableConfigurationProperties({PushConfig.class })
public class PushAutoConfiguration {

    @Autowired
    private PushConfig pushConfig;

    @Bean
    @ConditionalOnProperty(name = "push-stream.keepalive", havingValue = "true")
    public KeepAliveHandler keepAlive() {
        KeepAliveHandler keepAliveHandler = new KeepAliveHandler();
        keepAliveHandler.start();
        return keepAliveHandler;
    }

    @Bean
    @ConditionalOnProperty(name = "push-stream.keepalive", havingValue = "true")
    public ProcessWatchHandler processWatch() {
        ProcessWatchHandler processWatchHandler = new ProcessWatchHandler();
        processWatchHandler.start();
        return processWatchHandler;
    }

    @Bean
    public ApplicationContextRegister applicationContextRegister(){
        return new ApplicationContextRegister();
    }

    @Bean
    public CommandManager commandManager(){
        return new CommandManagerImpl();
    }

    @Bean
    public TaskDao taskDao(){
        return new TaskDaoImpl(pushConfig.getTaskSize());
    }

    @Bean
    public TaskHandler taskHandler(){
        return new TaskHandlerImpl();
    }


}
