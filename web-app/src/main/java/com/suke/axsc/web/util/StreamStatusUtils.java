package com.suke.axsc.web.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author czx
 * @title: StreamStatusUtls
 * @projectName push-stream
 * @description: TODO 流状态保存
 * @date 2022/10/1716:47
 */
public class StreamStatusUtils {

    /**
     * 保存状态
     **/
    private static final ConcurrentHashMap<String, Boolean> streamStatus = new ConcurrentHashMap<>();

    /**
     * 保存启动时间
     **/
    private static final ConcurrentHashMap<String, String> streamStartTime = new ConcurrentHashMap<>();


    public static ConcurrentHashMap getAllStreamStatus(){
        return streamStatus;
    }

    public static ConcurrentHashMap getAllStreamStartTime(){
        return streamStartTime;
    }

    public synchronized static void setStopAll(){
        for (String stream : streamStatus.keySet()){
            setStatus(stream,false);
        }
    }

    public synchronized static void setStatus(String stream, Boolean status) {
        streamStatus.put(stream, status);
        if(!status){
            setStartTime(stream,"未启动");
        }
    }

    public static Boolean getStatus(String stream) {
        return streamStatus.getOrDefault(stream, false);
    }

    public synchronized static void setStartTime(String stream, String startTime){
        streamStartTime.put(stream, startTime);
    }

    public static String getStartTime(String stream){
        return streamStartTime.getOrDefault(stream, "未启动");
    }

}
