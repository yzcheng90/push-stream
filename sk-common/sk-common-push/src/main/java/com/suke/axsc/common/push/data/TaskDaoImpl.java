package com.suke.axsc.common.push.data;

import com.suke.axsc.common.push.config.PushConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 任务信息持久层实现
 *
 * @author eguid
 * @version 2016年10月29日
 * @since jdk1.7
 *
 * @author czx
 * @date 2022-9-29
 */
@Slf4j
@Component
public class TaskDaoImpl implements TaskDao {

    @Autowired
    public PushConfig pushConfig;

    // 存放任务信息
    private ConcurrentMap<String, CommandTasker> map;

    public TaskDaoImpl(int taskSize) {
        map = new ConcurrentHashMap<>(taskSize);
    }

    @Override
    public CommandTasker get(String id) {
        return map.get(id);
    }

    @Override
    public Collection<CommandTasker> getAll() {
        return map.values();
    }

    @Override
    public int add(CommandTasker CommandTasker) {
        String id = CommandTasker.getId();
        if (id != null && !map.containsKey(id)) {
            map.put(CommandTasker.getId(), CommandTasker);
            if (map.get(id) != null) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int remove(String id) {
        if (map.remove(id) != null) {
            return 1;
        }
        ;
        return 0;
    }

    @Override
    public int removeAll() {
        int size = map.size();
        try {
            map.clear();
        } catch (Exception e) {
            return 0;
        }
        return size;
    }

    @Override
    public boolean isHave(String id) {
        return map.containsKey(id);
    }

}
