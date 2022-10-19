package com.suke.axsc.common.push.handler;


import com.suke.axsc.common.push.util.ApplicationContextRegister;
import com.suke.axsc.common.push.config.PushConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 任务消息输出处理器
 *
 * @author eguid
 * @version 2017年10月13日
 * @since jdk1.7
 *
 * @author czx
 * @date 2022-9-29
 */
@Data
@Slf4j
public class OutHandler extends Thread {

    /**
     * 控制状态
     */
    private volatile boolean desstatus = true;

    /**
     * 读取输出流
     */
    private BufferedReader br;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 消息处理方法
     */
    private OutHandlerMethod ohm;

    /**
     * 创建输出线程（默认立即开启线程）
     *
     * @param is
     * @param id
     * @param ohm
     * @return
     */
    public static OutHandler create(InputStream is, String id, OutHandlerMethod ohm) {
        return create(is, id, ohm, true);
    }

    /**
     * 创建输出线程
     *
     * @param is
     * @param id
     * @param ohm
     * @param start-是否立即开启线程
     * @return
     */
    public static OutHandler create(InputStream is, String id, OutHandlerMethod ohm, boolean start) {
        OutHandler out = new OutHandler(is, id, ohm);
        if (start){
            out.start();
        }
        return out;
    }

    public OutHandler(InputStream is, String id, OutHandlerMethod ohm) {
        br = new BufferedReader(new InputStreamReader(is));
        this.taskId = id;
        this.ohm = ohm;
    }

    /**
     * 重写线程销毁方法，安全的关闭线程
     */
    @Override
    public void destroy() {
        desstatus = false;
    }

    /**
     * 执行输出线程
     */
    @Override
    public void run() {
        PushConfig pushConfig = ApplicationContextRegister.getApplicationContext().getBean(PushConfig.class);
        String msg = null;
        try {
            if (pushConfig.isDebug()) {
                log.info(taskId + "开始推流！");
            }
            while (desstatus && (msg = br.readLine()) != null) {
                ohm.parse(taskId, msg);
                if (ohm.isBroken()) {
                    log.error("检测到中断，提交重启任务给保活处理器");
                    //如果发生异常中断，立即进行保活
                    //把中断的任务交给保活处理器进行进一步处理
                    KeepAliveHandler.add(taskId);
                }
            }
        } catch (IOException e) {
            log.error("发生内部异常错误，自动关闭[" + this.getId() + "]线程");
            destroy();
        } finally {
            if (this.isAlive()) {
                destroy();
            }
        }
    }

}
