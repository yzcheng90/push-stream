package com.suke.axsc.common.push.data;

import java.util.Collection;

/**
 * 任务信息持久层接口
 *
 * @author eguid
 * @version 2016年10月29日
 * @since jdk1.7
 * @author czx
 * @date 2022-9-29
 */
public interface TaskDao {
    /**
     * 通过id查询任务信息
     *
     * @param id - 任务ID
     * @return CommandTasker -任务实体
     */
    CommandTasker get(String id);

    /**
     * 查询全部任务信息
     *
     * @return Collection<CommandTasker>
     */
    Collection<CommandTasker> getAll();

    /**
     * 增加任务信息
     *
     * @param CommandTasker -任务信息实体
     * @return 增加数量：<1-增加失败，>=1-增加成功
     */
    int add(CommandTasker CommandTasker);

    /**
     * 删除id对应的任务信息
     *
     * @param id
     * @return 数量：<1-操作失败，>=1-操作成功
     */
    int remove(String id);

    /**
     * 删除全部任务信息
     *
     * @return 数量：<1-操作失败，>=1-操作成功
     */
    int removeAll();

    /**
     * 是否存在某个ID
     *
     * @param id - 任务ID
     * @return true:存在，false：不存在
     */
    boolean isHave(String id);
}
