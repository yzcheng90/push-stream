package com.suke.axsc.common.push.util;


import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.common.push.handler.OutHandler;
import com.suke.axsc.common.push.handler.OutHandlerMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 命令行操作工具类
 *
 * @author eguid
 *
 * @author czx
 * @date 2022-9-29
 */
@Slf4j
public class ExecUtil {

    /**
     * 执行命令行并获取进程
     *
     * @param cmd
     * @return
     * @throws IOException
     */
    public static Process exec(String cmd) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);// 执行命令获取主进程
        return process;
    }

    /**
     * 销毁进程
     *
     * @param process
     * @return
     */
    public static boolean stop(Process process) {
        if (process != null) {
            process.destroy();
            return true;
        }
        return false;
    }

    /**
     * 销毁输出线程
     *
     * @param outHandler
     * @return
     */
    public static boolean stop(Thread outHandler) {
        if (outHandler != null && outHandler.isAlive()) {
            try {
                outHandler.stop();
                outHandler.destroy();
            }catch (Exception e){
                log.error("线程销毁失败：{}",e.getMessage());
            }
            return true;
        }
        return false;
    }

    /**
     * 销毁
     */
    public static void stop(CommandTasker tasker) {
        if (tasker != null) {
            stop(tasker.getThread());
            stop(tasker.getProcess());
        }
    }

    /**
     * 创建命令行任务
     *
     * @param id
     * @param command
     * @return
     * @throws IOException
     */
    public static CommandTasker createTasker(String id, String command, OutHandlerMethod ohm) throws IOException {
        // 执行本地命令获取任务主进程
        Process process = exec(command);
        // 创建输出线程
        OutHandler outHandler = OutHandler.create(process.getErrorStream(), id, ohm);
        log.info("任务ID：{},使用输出线程：{}",id,outHandler.getName());
        CommandTasker tasker = new CommandTasker(id, command, process, outHandler);
        return tasker;
    }

    /**
     * 中断故障缘故重启
     *
     * @param tasker
     * @return
     * @throws IOException
     */
    public static CommandTasker restart(CommandTasker tasker) throws IOException {
        if (tasker != null) {
            String id = tasker.getId(), command = tasker.getCommand();
            OutHandlerMethod ohm = null;
            if (tasker.getThread() != null) {
                ohm = tasker.getThread().getOhm();
            }

            //安全销毁命令行进程和输出子线程
            stop(tasker);
            // 执行本地命令获取任务主进程
            Process process = exec(command);
            tasker.setProcess(process);
            // 创建输出线程
            OutHandler outHandler = OutHandler.create(process.getErrorStream(), id, ohm);
            tasker.setThread(outHandler);
        }else {
            log.error("可能是手动停止操作，无需启动");
        }
        return tasker;
    }
}
