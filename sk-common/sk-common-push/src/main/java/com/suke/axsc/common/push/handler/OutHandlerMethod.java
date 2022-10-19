package com.suke.axsc.common.push.handler;
/**
 * 输出消息处理
 * @author eguid
 * @since jdk1.7
 * @version 2017年10月13日
 *
 * @author czx
 * @date 2022-9-29
 */
public interface OutHandlerMethod {
	/**
	 * 解析消息
	 * @param id-任务ID
	 * @param msg -消息
	 */
	void parse(String id, String msg);

	/**
	 * 任务是否异常中断
	 * @return
	 */
	boolean isBroken();
}
