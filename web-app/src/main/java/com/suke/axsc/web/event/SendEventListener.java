package com.suke.axsc.web.event;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suke.axsc.web.component.NettyWebSocketServer;
import com.suke.axsc.web.component.SocketParam;
import com.suke.axsc.web.component.SocketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author czx
 * @title: SendEventListener
 * @projectName zhjg
 * @description: TODO
 * @date 2021/4/1915:53
 */
@Slf4j
@Component
public class SendEventListener implements ApplicationListener<SendEvent> {

    public ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public NettyWebSocketServer nettyWebSocketServer;

    @Override
    public void onApplicationEvent(SendEvent sendEvent) {
        try {
            SocketParam param = new SocketParam();
            param.setType(SocketType.TYPE_LOG);
            param.setData(DateUtil.now() +"："+sendEvent.getMsg());
            String data = objectMapper.writeValueAsString(param);
            nettyWebSocketServer.sendAll(data);
        } catch (IOException e) {
            log.error("SendEventListener 发送失败：{}",e.getMessage());
        }
    }

}
