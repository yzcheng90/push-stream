package com.suke.axsc.web.stream.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


/**
 * 推流管理
 * @author czx
 * @email object_czx@163.com
 * @date 2022-09-30 09:57:47
 */
@Data
@TableName("stream_source")
public class StreamSource implements Serializable {
	private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @JsonProperty(value = "streamId")
	public String streamId;

    /**
     * 流名称
     **/
    @JsonProperty(value = "streamName")
	public String streamName;
    /**
     * 摄像头RTSP地址
     **/
    @JsonProperty(value = "streamUrl")
	public String streamUrl;
    /**
     * nginx服务地址
     **/
    @JsonProperty(value = "streamPushUrl")
	public String streamPushUrl;
    /**
     * ffmpeg命令
     **/
    @JsonProperty(value = "streamCmd")
	public String streamCmd;
    /**
     * 推流状态
     **/
    @JsonProperty(value = "streamStatus")
	public String streamStatus;
    /**
     * 流类型（live:直播，tracks:回放）
     **/
    @JsonProperty(value = "streamType")
	public String streamType;
    /**
     * 已推推流时长
     **/
    @TableField(exist = false)
    @JsonProperty(value = "timeLong")
	public String timeLong;
    /**
     * 订阅人数
     **/
    @TableField(exist = false)
    @JsonProperty(value = "subscribe")
	public int subscribe;
    /**
     * 推流开始时间
     **/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty(value = "startTime")
	public Date startTime;


}
