package com.suke.axsc.web.component;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suke.axsc.web.handler.CommandManagerHandler;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author czx
 * @title: NettyWebSocketServer
 * @projectName push-stream
 * @description: TODO
 * @date 2022/10/1317:25
 */
@Slf4j
@Component
@ServerEndpoint(host = "${ws.host}",port = "${ws.port}",path = "/websocket/{sid}")
public class NettyWebSocketServer {

    @Autowired
    public CommandManagerHandler commandManagerHandler;

    public static ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 全部连接
     **/
    public static Map<String, Session> userMap = new ConcurrentHashMap<>();
    /**
     *  存储是否接收日志消息
     **/
    public static Map<String, Boolean> userAgreeMessage = new ConcurrentHashMap<>();

    /**
     * 直播流订阅
     * Key : streamId
     * Value : userId
     **/
    public static Map<String, List<String>> liveSubscribe = new ConcurrentHashMap<>();

    private String userId;

    /**
     * 获取所有订阅数据
     **/
    public Map<String, List<String>> getLiveSubscribeList(){
        return this.liveSubscribe;
    }

    /**
     * 获取订阅数据
     **/
    public List<String> getLiveSubscribeList(String streamId){
        return this.liveSubscribe.get(streamId);
    }

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String message,@PathVariable String sid){
        userMap.put(sid,session);
        userAgreeMessage.put(sid,true);
        this.userId = sid;
        log.info("NettyWebSocket[{}],连接成功",sid);
        SocketParam param = new SocketParam();
        param.setType(SocketType.TYPE_CONNECT);
        param.setData(sid + "连接成功");

        try {
            this.send(session,sid, objectMapper.writeValueAsString(param));
        } catch (JsonProcessingException e) {
            log.info("NettyWebSocket[{}],onOpen:发送失败",sid);
        }

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        log.info("NettyWebSocket[{}],退出连接",userId);
        // 删除用户
        userMap.remove(userId);
        // 删除用户日志消息状态
        userAgreeMessage.remove(userId);
        // 删除用户订阅
        this.userLogoutUnsubscribe(userId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("NettyWebSocket[{}],{}",userId,throwable.getMessage());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            SocketParam socketParam = objectMapper.readValue(message, SocketParam.class);
            if (socketParam != null) {
                switch (socketParam.getType()) {
                    // 日志
                    case SocketType.TYPE_LOG:
                        if (socketParam.getData().equals("true")) {
                            userAgreeMessage.put(userId, true);
                            log.info("NettyWebSocket[{}],打开日志订阅",userId);
                        } else if (socketParam.getData().equals("false")) {
                            userAgreeMessage.put(userId, false);
                            log.info("NettyWebSocket[{}],关闭日志订阅",userId);
                        }
                        break;
                     //连接
                    case SocketType.TYPE_CONNECT:
                        break;
                    //订阅直播
                    case SocketType.TYPE_SUBSCRIBE_LIVE:
                        this.subscribe(userId,socketParam.getData(),null);
                        log.info("NettyWebSocket[{}],订阅[{}]直播",userId,socketParam.getData());
                        break;
                    //取消订阅直播
                    case SocketType.TYPE_UNSUBSCRIBE_LIVE:
                        this.unsubscribe(userId,socketParam.getData());
                        log.info("NettyWebSocket[{}],取消订阅[{}]直播",userId,socketParam.getData());
                        break;
                    //订阅回放
                    case SocketType.TYPE_SUBSCRIBE_TRACKS:
                        String[] param = socketParam.getData().split("&");
                        if(param != null && param.length == 2){
                            this.subscribe(userId,param[0],param[1]);
                            log.info("NettyWebSocket[{}],订阅[{}]回放",userId,socketParam.getData());
                        }else {
                            log.error("NettyWebSocket[{}],订阅回放失败，参数错误:{}",userId,socketParam.getData());
                        }
                        break;
                    //取消订阅回放
                    case SocketType.TYPE_UNSUBSCRIBE_TRACKS:
                        this.unsubscribe(userId,socketParam.getData());
                        log.info("NettyWebSocket[{}],取消订阅[{}]回放",userId,socketParam.getData());
                        break;
                }
            }
        } catch (Exception e) {
            log.error("参数解析错误：{}", e.getMessage());
        }
    }

    public void send(Session currSession,String sid,String message){
        try {
            currSession.sendText(message);
        } catch (Exception e) {
            log.error("NettyWebSocket[{}] send 发送错误：{}", sid, e.getMessage());
        }
    }

    public void sendAll(String message) {
        for (String sid : userMap.keySet()) {
            try {
                Session currSession = userMap.get(sid);
                if(userAgreeMessage.size() != 0){
                    if (currSession != null && userAgreeMessage.get(sid) != null && userAgreeMessage.get(sid)) {
                        currSession.sendText(message);
                    }
                }else {
                    currSession.sendText(message);
                }
            } catch (Exception e) {
                log.error("NettyWebSocket[{}] sendAll 发送错误：{}", sid, e.getMessage());
            }
        }
    }

    /**
     * 订阅直播
     * @param startTime 回放使用参数
     **/
    public void subscribe(String userId,String streamId,String startTime){
        // 第一次
        if(liveSubscribe.size() == 0){
            this.subscribeLive(userId,streamId,startTime);
        }else {
            List<String> list = liveSubscribe.get(streamId);
            // 当前直播没有人订阅
            if(CollUtil.isEmpty(list)){
                this.subscribeLive(userId,streamId,startTime);
            }else {
                // 已有人订阅
                // 如果已订阅-直接发送消息
                // 如果未订阅-添加用户订阅
                if(list.contains(userId)){
                    this.liveNotice(userId,streamId,"已订阅，请勿重复订阅");
                }else {
                    list.add(userId);
                    liveSubscribe.put(streamId,list);
                    this.liveNotice(userId,streamId,"订阅成功");
                }
            }
        }
    }

    /**
     * 订阅直播 | 回放
     **/
    public void subscribeLive(String userId,String streamId,String startTime){
        // 保存订阅用户
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        // 保存订阅用户
        liveSubscribe.put(streamId,userIds);
        // 启动推流服务
        commandManagerHandler.push(streamId,startTime);
        // 通知前台显示
        this.liveNotice(userId,streamId,"订阅成功");
    }

    /**
     * 取消订阅 直播|回放
     **/
    public void unsubscribe(String userId,String streamId){
        List<String> list = liveSubscribe.get(streamId);
        if(CollUtil.isNotEmpty(list)){
            if(list.size() == 1){
                // 如果是最后一人，直接删除直播
                liveSubscribe.remove(streamId);
                // 停止推流
                commandManagerHandler.stop(streamId);
            }else {
                // 如果不是最后一人，删除用户
                list.remove(userId);
                liveSubscribe.put(streamId,list);
            }
        }
    }

    /**
     * 用户退出，取消订阅
     **/
    public void userLogoutUnsubscribe(String userId){
        for (Map.Entry<String, List<String>> entry : liveSubscribe.entrySet()) {
            String streamId = entry.getKey();
            List<String> userList = entry.getValue();
            if(CollUtil.isNotEmpty(userList) && userList.contains(userId)){
                this.unsubscribe(userId,streamId);
            }
        }
    }

    /**
     * 订阅通知
     **/
    public void liveNotice(String userId,String streamId,String msg){
        Session currSession = userMap.get(userId);
        SocketParam param = new SocketParam();
        param.setType(SocketType.TYPE_SUBSCRIBE_LIVE);
        param.setData("["+userId + "]"+msg+"："+streamId);
        try {
            currSession.sendText(objectMapper.writeValueAsString(param));
        } catch (JsonProcessingException e) {
            log.error("[{}] subscribe发送失败",userId);
        }
    }
}
