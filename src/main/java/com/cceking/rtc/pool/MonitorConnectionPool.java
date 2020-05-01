package com.cceking.rtc.pool;

import com.cceking.rtc.model.MonitorConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理用户的登录和退出
 */
public class MonitorConnectionPool {

    //保存连接的MAP容器
    private static final Map<String, MonitorConnection> connections = new HashMap<>();

    /**
     * 当前连接的用户名称
     */
    private final String monitorId;

    public MonitorConnectionPool() {
        this.monitorId = "monitorId";
    }

    public MonitorConnectionPool(String monitorId) {
        this.monitorId = monitorId;
    }

    public static MonitorConnection getMonitorConnection(String roomId, String userId){
        if ( !connections.containsKey(createId(roomId, userId))){
            addMonitorConnection(roomId, userId);
        }
        return connections.get(createId(roomId, userId));
    }

    public static void addMonitorConnection(MonitorConnection mc){
        connections.put(mc.getId(), mc);
    }

    public static void addMonitorConnection(String roomId, String userId){
        connections.put(createId(roomId, userId), new MonitorConnection(roomId, userId));
    }

    public static void removeMonitorConnection(MonitorConnection mc){
        connections.remove(mc.getId());
    }

    public static String createId(String roomId, String userId){
        return roomId+"-"+userId;
    }

    public static void sendBinaryToUser(String keyId, ByteBuffer message){
        try {
            //向特定的用户发送数据
            System.out.println("send message to user : " + keyId + " ,message content : " + message);
            MonitorConnection inbound = connections.get(keyId);
            if(inbound != null){
                inbound.getWsOutbound().writeBinaryMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  