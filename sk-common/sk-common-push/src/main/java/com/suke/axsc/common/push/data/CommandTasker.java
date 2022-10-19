package com.suke.axsc.common.push.data;


import com.suke.axsc.common.push.handler.OutHandler;
import lombok.Data;

/**
 * 用于存放任务id,任务主进程，任务输出线程
 *
 * @author eguid
 * @version 2016年10月29日
 * @since jdk1.7
 * @author czx
 * @date 2022-9-29
 */
@Data
public class CommandTasker {
    private final String id;// 任务id
    private final String command;//命令行
    private Process process;// 命令行运行主进程
    private OutHandler thread;// 命令行消息输出子线程

    public CommandTasker(String id, String command) {
        this.id = id;
        this.command = command;
    }

    public CommandTasker(String id, String command, Process process, OutHandler thread) {
        this.id = id;
        this.command = command;
        this.process = process;
        this.thread = thread;
    }

}
