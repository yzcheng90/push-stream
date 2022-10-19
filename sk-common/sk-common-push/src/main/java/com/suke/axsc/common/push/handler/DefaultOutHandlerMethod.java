package com.suke.axsc.common.push.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认任务消息输出处理
 *
 * @author eguid
 * @author czx
 * @version 2017年10月13日
 * @date 2022-9-29
 * @since jdk1.7
 */
@Slf4j
@Component
public abstract class DefaultOutHandlerMethod implements OutHandlerMethod {

    @Override
    public void parse(String id, String msg) {

    }

    @Override
    public boolean isBroken() {
        return false;
    }

}
