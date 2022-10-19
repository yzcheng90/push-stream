package com.suke.axsc.web.handler;

import cn.hutool.core.util.ObjectUtil;
import com.suke.axsc.common.push.cmd.CommandManager;
import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.web.stream.entity.StreamSource;
import com.suke.axsc.web.stream.mapper.StreamSourceMapper;
import com.suke.axsc.web.util.StreamStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author czx
 * @title: CommandManagerHandler
 * @projectName push-stream
 * @description: TODO 推流操作类
 * @date 2022/10/1413:56
 */
@Slf4j
@Component
public class CommandManagerHandler {

    @Autowired
    public CommandManager commandManager;

    @Autowired
    public StreamSourceMapper streamSourceMapper;

    public Collection<CommandTasker> queryAll() {
        return commandManager.queryAll();
    }

    public String start(String id, String commond) {
        // 设置默认状态为未启动
        StreamStatusUtils.setStatus(id,false);
        return commandManager.start(id, commond);
    }

    public CommandTasker query(String id) {
        return commandManager.query(id);
    }

    public int stopAll() {
        // 设置默认状态为未启动
        StreamStatusUtils.setStopAll();
        return commandManager.stopAll();
    }

    public boolean push(String streamId, String startTime) {
        StreamSource entity = streamSourceMapper.selectById(streamId);
        if (ObjectUtil.isNotNull(entity)) {
            String cmd = "";
            if (entity.getStreamType().equals("live")) { // 直播
                cmd = String.format(entity.getStreamCmd(), entity.getStreamUrl(), entity.getStreamPushUrl());
            } else if (entity.getStreamType().equals("tracks")) { // 回放
                cmd = String.format(entity.getStreamCmd(), entity.getStreamUrl() + "?starttime=" + startTime, entity.getStreamPushUrl());
            }

            commandManager.start(entity.getStreamName(), cmd);
            // 设置默认状态为未启动
            StreamStatusUtils.setStatus(entity.getStreamName(),false);
            return true;
        }
        return false;
    }

    public boolean stop(String streamId) {
        StreamSource entity = streamSourceMapper.selectById(streamId);
        if (ObjectUtil.isNotNull(entity)) {
            boolean stop = commandManager.stop(entity.getStreamName());
            log.info("Task：{},推流停止操作：{}", entity.getStreamName(), stop);
            entity.setStreamStatus(!stop ? "推流中" : "未推流");
            streamSourceMapper.updateById(entity);
            // 设置默认状态为未启动
            StreamStatusUtils.setStatus(entity.getStreamName(),false);
        }
        return true;
    }

}
