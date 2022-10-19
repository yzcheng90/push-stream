package com.suke.axsc.common.push.cmd;

import com.suke.axsc.common.push.config.PushConfig;
import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.common.push.data.TaskDao;
import com.suke.axsc.common.push.handler.KeepAliveHandler;
import com.suke.axsc.common.push.handler.ProcessWatchHandler;
import com.suke.axsc.common.push.handler.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * FFmpeg命令操作管理器
 *
 * @author eguid
 * @version 2017年10月13日
 * @since jdk1.7
 * @author czx
 * @date 2022-9-29
 */
@Slf4j
@Component
public class CommandManagerImpl implements CommandManager {

    @Autowired
    public PushConfig pushConfig;

    /**
     * 任务持久化器
     */
    @Autowired
    private TaskDao taskDao;
    /**
     * 任务执行处理器
     */
    @Autowired
    private TaskHandler taskHandler;

    /**
     * 保活处理器
     */
    @Autowired
    private KeepAliveHandler keepAliveHandler;

    /**
     * 保活处理器
     */
    @Autowired
    private ProcessWatchHandler processWatchHandler;


	@Override
    public String start(String id, String command) {
        return start(id, command, false);
    }

    @Override
    public String start(String id, String command, boolean hasPath) {

        if (id != null && command != null) {
            CommandTasker tasker = taskHandler.process(id, hasPath ? command : pushConfig.getFfmpegPath() + command);
            if (tasker != null) {
                int ret = taskDao.add(tasker);
                if (ret > 0) {
                    return tasker.getId();
                } else {
                    // 持久化信息失败，停止处理
                    taskHandler.stop(tasker.getProcess(), tasker.getThread());
                    if (pushConfig.isDebug()){
                        log.error("持久化失败，停止任务！");
                    }
                }
            }
        }
        return null;
    }


    @Override
    public boolean stop(String id) {
        if (id != null && taskDao.isHave(id)) {
            if (pushConfig.isDebug()){
                log.info("正在停止任务：" + id);
            }
            CommandTasker tasker = taskDao.get(id);
            if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
                taskDao.remove(id);
                return true;
            }
        }
        log.error("停止任务失败！id=" + id);
        return false;
    }

    @Override
    public int stopAll() {
        Collection<CommandTasker> list = taskDao.getAll();
        Iterator<CommandTasker> iter = list.iterator();
        CommandTasker tasker = null;
        int index = 0;
        while (iter.hasNext()) {
            tasker = iter.next();
            if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
                taskDao.remove(tasker.getId());
                index++;
            }
        }
        if (pushConfig.isDebug()){
            log.info("停止了{}个任务！",index);
        }
        return index;
    }

    @Override
    public CommandTasker query(String id) {
        return taskDao.get(id);
    }

    @Override
    public Collection<CommandTasker> queryAll() {
        return taskDao.getAll();
    }

    @Override
    public void destory() {
        if (keepAliveHandler != null) {
            //安全停止保活线程
            keepAliveHandler.interrupt();
        }
        if (processWatchHandler != null) {
            //安全停止监控进程
            processWatchHandler.interrupt();
        }
    }
}
