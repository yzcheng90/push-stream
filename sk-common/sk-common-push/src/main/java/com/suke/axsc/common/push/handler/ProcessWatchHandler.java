package com.suke.axsc.common.push.handler;

import cn.hutool.core.io.IoUtil;
import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.common.push.data.TaskDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * @author czx
 * @title: ProcessWatchHandler
 * @projectName push-stream
 * @description: TODO  Process 监控
 * @date 2022/9/3010:59
 */
@Slf4j
@Component
public class ProcessWatchHandler extends Thread{

    /**
     * 执行管理器
     */
    @Autowired
    private TaskDao taskDao;

    public volatile int stop_index = 0;//安全停止线程标记

    @Override
    public void run() {
        for (; stop_index == 0; ) {
            String id = null;
            Process process = null;
            Collection<CommandTasker> commandTaskers = null;
            try {
                if(taskDao != null){
                    commandTaskers = taskDao.getAll();
                    for(CommandTasker temp : commandTaskers){
                        id = temp.getId();
                        process = temp.getProcess();
                        if(process != null){
                            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                            String currLog = br.readLine();
                            if(currLog == null){
                                log.error("taskId:{} 进程可能挂了，没有日志输出",id);
                                // 添加进保活线程
                                KeepAliveHandler.add(id);
                                // 休息1分钟
                                Thread.sleep(60*1000);
                            }else {
                                if(!process.isAlive() && currLog.length() < 10){
                                    String taskId = KeepAliveHandler.get();
                                    if(taskId == null){
                                        log.error("taskId:{} 进程挂了",id);
                                        // 添加进保活线程
                                        KeepAliveHandler.add(id);
                                    }
                                    // 休息1分钟
                                    Thread.sleep(60*1000);
                                }
                            }
                        }
                    }
                }
                try {
                    // 无限循环太快，设置为一秒一次
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            } catch (Exception e) {
                log.info(id + " 任务进程获取失败：{}",e.getMessage());
                // 休息1分钟
                try {
                    Thread.sleep(60*1000);
                } catch (InterruptedException ex) {

                }
            }
        }
    }

    @Override
    public void interrupt() {
        stop_index = 1;
    }

}
