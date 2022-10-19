package com.suke.axsc.web.util;

import com.suke.axsc.common.push.util.ApplicationContextRegister;
import com.suke.axsc.web.event.SendEvent;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;

/**
 * @author czx
 * @title: LogUtils
 * @projectName yz
 * @description: TODO
 * @date 2020/3/916:11
 */
@UtilityClass
public class LogUtils {

    public void send(String msg){
        ApplicationContext applicationContext = ApplicationContextRegister.getApplicationContext();
        applicationContext.publishEvent(new SendEvent(applicationContext,msg));
    }
}
