package com.suke.axsc.common.push.handler;

import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.common.push.data.TaskDao;
import com.suke.axsc.common.push.util.ExecUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 任务保活处理器（一个后台保活线程，用于处理异常中断的持久任务）
 *
 * @author eguid
 * @author czx
 * @date 2022-9-29
 */
@Slf4j
@Component
public class KeepAliveHandler extends Thread {
    /**
     * 待处理队列
     */
    private static Queue<String> queue = new ConcurrentLinkedQueue<>();

    public int err_index = 0;//错误计数

    public volatile int stop_index = 0;//安全停止线程标记

    /**
     * 任务持久化器
     */
    @Autowired
    private TaskDao taskDao;


    public static void add(String id) {
        if (queue != null) {
            queue.offer(id);
        }
    }


    public static String get() {
        if (queue != null) {
            return queue.peek();
        }
        return null;
    }

    public boolean stop(Process process) {
        if (process != null) {
            process.destroy();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        for (; stop_index == 0; ) {
            if (queue == null) {
                continue;
            }
            String id = null;
            CommandTasker task = null;

            try {
                while (queue.peek() != null) {
                    log.error("准备重启任务：" + queue);
                    id = queue.poll();
                    task = taskDao.get(id);
                    //重启任务
                    ExecUtil.restart(task);
                }
            } catch (IOException e) {
                log.error(id + " 任务重启失败，详情：" + task);
                //重启任务失败
                err_index++;
            } catch (Exception e) {

            }
            try {
                // 无限循环太快，设置为一秒一次
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void interrupt() {
        stop_index = 1;
    }

}
