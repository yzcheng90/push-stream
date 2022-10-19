package com.suke.axsc.web.component;

/**
 * @author czx
 * @title: SocketType
 * @projectName push-stream
 * @description: TODO
 * @date 2022/10/1315:29
 */
public interface SocketType {

    String TYPE_LOG = "type_log";
    String TYPE_CONNECT = "type_connect";
    String TYPE_SUBSCRIBE_LIVE = "type_subscribe_live";
    String TYPE_UNSUBSCRIBE_LIVE = "type_unsubscribe_live";
    String TYPE_SUBSCRIBE_TRACKS = "type_subscribe_tracks";
    String TYPE_UNSUBSCRIBE_TRACKS = "type_unsubscribe_tracks";

}
