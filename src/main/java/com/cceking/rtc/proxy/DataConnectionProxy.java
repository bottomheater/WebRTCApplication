package com.cceking.rtc.proxy;

import com.cceking.rtc.model.DataConnection;
import com.cceking.rtc.pool.DataConnectionPool;

import java.util.HashMap;
import java.util.Map;

public class DataConnectionProxy {
    //保存连接的MAP容器
    private static final Map<String, DataConnectionPool> connections = new HashMap<>();


    public static DataConnection getDataConnection(String roomId, String from, String to) {
        if (!connections.containsKey(roomId)) {
            connections.put(roomId, new DataConnectionPool(roomId));
        }
        DataConnectionPool ucp = connections.get(roomId);
        if (!ucp.contains(from, to)) {
            ucp.addMessageInbound(new DataConnection(from, to, roomId));
        }
        return ucp.get(from, to);
    }

    public static DataConnectionPool getDataConnectionPool(String roomId) {
        if (!connections.containsKey(roomId)) {
            connections.put(roomId, new DataConnectionPool(roomId));
        }
        DataConnectionPool dcp = connections.get(roomId);
        return dcp;
    }
}
