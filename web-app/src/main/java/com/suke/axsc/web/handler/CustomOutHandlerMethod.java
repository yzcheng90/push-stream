package com.suke.axsc.web.handler;

import com.suke.axsc.common.push.handler.DefaultOutHandlerMethod;
import com.suke.axsc.web.util.LogUtils;
import com.suke.axsc.web.util.StreamStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author czx
 * @title: CustomOutHandlerMethod
 * @projectName push-stream
 * @description: TODO 重写默认的输出线程，输出日志
 * @date 2022/9/2917:42
 */
@Slf4j
@Component
public class CustomOutHandlerMethod extends DefaultOutHandlerMethod {

    /**
     * 任务是否异常中断，如果
     */
    public boolean isBroken = false;

    @Override
    public void parse(String streamName, String msg) {
        if(msg != null && msg.trim().length() != 0){
            LogUtils.send("["+streamName+"]-"+msg);
            //过滤消息
            if (msg.indexOf("fail") != -1) {
                log.error(streamName + "任务可能发生故障：" + msg);
                log.error("失败，设置中断状态");
                StreamStatusUtils.setStatus(streamName,false);
                isBroken = true;
            } else if (msg.indexOf("miss") != -1) {
                log.error(streamName + "任务可能发生丢包：" + msg);
                log.error("失败，设置中断状态");
                StreamStatusUtils.setStatus(streamName,false);
                isBroken = true;
            } else if(msg.contains("Unknown error")){
                log.error(streamName + "任务可能发生网络故障：" + msg);
                log.error("失败，设置中断状态");
                StreamStatusUtils.setStatus(streamName,false);
                isBroken = true;
            } else {
                isBroken = false;
                if(msg.trim().indexOf("frame") != -1){
                    // 设置启动状态
                    StreamStatusUtils.setStatus(streamName,true);
                    // 设置启动时间
                    if(msg.indexOf("time=") > 0){
                        String time = msg.substring(msg.indexOf("time=") + 5, msg.indexOf("bitrate="));
                        StreamStatusUtils.setStartTime(streamName,time);
                    }
                }else if (msg.indexOf("Error") != -1) {
                    log.error("[{}] ： {}",streamName,msg);
                    StreamStatusUtils.setStatus(streamName,false);
                } else {
                    log.info(streamName + "日志：" + msg);
                    StreamStatusUtils.setStatus(streamName,false);
                }
            }
        }
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
}
