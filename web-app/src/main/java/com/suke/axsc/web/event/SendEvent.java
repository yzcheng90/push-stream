package com.suke.axsc.web.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author czx
 * @title: SendEvent
 * @projectName zhjg
 * @description: TODO 异步事件发送短信
 * @date 2021/4/1915:48
 */
public class SendEvent extends ApplicationEvent {

    @Getter
    public String msg;

    public SendEvent(Object source,String msg) {
        super(source);
        this.msg = msg;
    }
}
