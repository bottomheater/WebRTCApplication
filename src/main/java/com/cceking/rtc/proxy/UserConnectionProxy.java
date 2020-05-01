package com.cceking.rtc.proxy;

import com.cceking.rtc.model.UserConnection;
import com.cceking.rtc.pool.UserConnectionPool;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理所有房间，
 */
public class UserConnectionProxy {
    //保存连接的MAP容器
    private static final Map<String, UserConnectionPool> connections = new HashMap<>();

    public static UserConnection getUserConnection(String roomId, String userId) {
        if (!connections.containsKey(roomId)) {
            connections.put(roomId, new UserConnectionPool(roomId));
        }
        UserConnectionPool ucp = connections.get(roomId);
        if (!ucp.contains(userId)) {
            ucp.addMessageInbound(new UserConnection(roomId, userId));
        }
        return ucp.get(userId);
    }

    public static UserConnectionPool getUserConnectionPool(String roomId) {
        if (!connections.containsKey(roomId)) {
            connections.put(roomId, new UserConnectionPool(roomId));
        }
        UserConnectionPool ucp = connections.get(roomId);
        return ucp;
    }

    public static JSONObject getUsers() {
        JSONObject result = new JSONObject();
        result.element("type", "monitor_user");
        result.element("users", connections);
        return result;
    }
}
