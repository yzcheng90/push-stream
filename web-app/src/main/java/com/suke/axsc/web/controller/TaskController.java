package com.suke.axsc.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.suke.axsc.common.push.data.CommandTasker;
import com.suke.axsc.web.component.NettyWebSocketServer;
import com.suke.axsc.web.handler.CommandManagerHandler;
import com.suke.axsc.web.stream.entity.StreamSource;
import com.suke.axsc.web.stream.service.StreamSourceService;
import com.suke.axsc.web.util.R;
import com.suke.axsc.web.util.StreamStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author czx
 * @title: TaskController
 * @projectName push-stream
 * @description: TODO
 * @date 2022/9/2913:40
 */
@Slf4j
@RestController
public class TaskController {

    @Autowired
    public CommandManagerHandler commandManagerHandler;

    @Autowired
    public StreamSourceService streamSourceService;

    @Autowired
    public NettyWebSocketServer nettyWebSocketServer;

    @RequestMapping(value = "/queryData",method = RequestMethod.GET)
    public R queryData(){
        List<StreamSource> list = streamSourceService.list();
        list.forEach(streamSource -> {
            String timeLong = StreamStatusUtils.getStartTime(streamSource.getStreamName());
            streamSource.setTimeLong(timeLong);
            boolean status = StreamStatusUtils.getStatus(streamSource.getStreamName());
            streamSource.setStreamStatus(String.valueOf(status));
        });
        return R.ok().setData(list);
    }

    @RequestMapping(value = "/subscribeList",method = RequestMethod.GET)
    public R subscribeList(){
        List<StreamSource> list = streamSourceService.list().stream().map(streamSource -> {
            List<String> liveSubscribeList = nettyWebSocketServer.getLiveSubscribeList(streamSource.getStreamId());
            streamSource.setSubscribe(CollUtil.isEmpty(liveSubscribeList) ? 0 :liveSubscribeList.size());
            return streamSource;
        }).collect(Collectors.toList());
        return R.ok().setData(list);
    }

    @RequestMapping(value = "/pushAll",method = RequestMethod.GET)
    public R pushAll(){
        List<StreamSource> list = streamSourceService.list(Wrappers.<StreamSource>query().lambda().eq(StreamSource::getStreamType,"live"));
        if(CollUtil.isEmpty(list)){
            log.info("推流数据为空");
            return R.error("推流数据为空");
        }
        for (StreamSource entry : list) {
            String cmd = String.format(entry.getStreamCmd(), entry.getStreamUrl(),entry.getStreamPushUrl());
            commandManagerHandler.start(entry.getStreamName(),cmd);
        }
        return R.ok();
    }

    @RequestMapping(value = "/push/{streamId}",method = RequestMethod.GET)
    public R push(@PathVariable("streamId") String streamId){
        commandManagerHandler.push(streamId,null);
        return R.ok();
    }

    @RequestMapping(value = "/getStatus/{streamName}",method = RequestMethod.GET)
    public R getStreamStatus(@PathVariable("streamName") String streamName){
        boolean status = StreamStatusUtils.getStatus(streamName);
        log.info("streamName:{},status:{}",streamName,status);
        return R.ok().setData(status);
    }

    @RequestMapping(value = "/query/{streamId}",method = RequestMethod.GET)
    public R query(@PathVariable("streamId") String streamId){
        StreamSource entity = streamSourceService.getById(streamId);
        if(ObjectUtil.isNotNull(entity)){
            CommandTasker query = commandManagerHandler.query(entity.getStreamName());
            log.info("推流查询：{}",query);
            boolean bool = ObjectUtil.isNotEmpty(query);
            entity.setStreamStatus(bool?"推流中":"未推流");
            streamSourceService.updateById(entity);
        }
        return R.ok();
    }

    @RequestMapping(value = "/stop/{streamId}",method = RequestMethod.GET)
    public R stop(@PathVariable("streamId") String streamId){
        boolean bool = commandManagerHandler.stop(streamId);
        return R.ok().setData(bool);
    }

    @RequestMapping(value = "/stopAll",method = RequestMethod.GET)
    public R stopAll(){
        int all = commandManagerHandler.stopAll();
        log.info("停止所有推流：{}",all);
        List<StreamSource> list = streamSourceService.list();
        list.forEach(entity->{
            entity.setStreamStatus("未推流");
            streamSourceService.updateById(entity);
        });
        return R.ok();
    }

    @RequestMapping(value = "/loadErrorLog",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public String loadErrorLog(){
        String fileName = "error.log";
        String path = System.getProperty("user.dir");
        FileReader fileReader = new FileReader(path+"/logs/"+ fileName);
        String result = fileReader.readString();
        return result;
    }

}
