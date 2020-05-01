package com.cceking.rtc.pool;

import com.cceking.rtc.model.UserConnection;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/***
 * 根据房间ID，保存该房间下的所用用户连接
 */
public class UserConnectionPool {

    private  String roomId;
  
    //保存连接的MAP容器  
    private static final Map<String, UserConnection> connections = new HashMap<>();

    public UserConnectionPool(String roomId){
        this.roomId = roomId;
    }

    //向连接池中添加连接
    public void addMessageInbound(UserConnection inbound){
        //添加连接
        System.out.println("user : " + inbound.getUser() + " login..");
        connections.put(inbound.getUser(), inbound);
    }

    //获取所有的在线用户
    public Set<String> getOnlineUser(){
        return connections.keySet();
    }

    public boolean contains(String user){
        return connections.containsKey(user);
    }

    public UserConnection get(String user){
        return connections.get(user);
    }

    public void removeMessageInbound(UserConnection inbound){
        //移除连接
        System.out.println("user : " + inbound.getUser() + " logout..");
        connections.remove(inbound.getUser());
    }

    //向特定的用户发送数据
    public void sendMessageToUser(String user,String message){
        try {
            System.out.println("send message to user : " + user + " ,message content : " + message);
            UserConnection inbound = connections.get(user);
            if(inbound != null){
                inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向所有的用户发送消息
    public void sendMessage(String message){
        try {
            Set<String> keySet = connections.keySet();
            for (String key : keySet) {
                UserConnection inbound = connections.get(key);
                if(inbound != null){
                    System.out.println("send message to user : " + key + " ,message content : " + message);
                    inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制带获取当前在线用户
     * @return
     */
    public static JSONObject getUsers(){
        JSONObject result = new JSONObject();
        result.element("type", "monitor_user");
        Set<String> keySet = connections.keySet();
        for (String key : keySet) {
            JSONObject obj = new JSONObject();
            UserConnection u = connections.get(key);
            obj.put("rid", u.getRoomId());
            obj.put("uId", u.getUser());
        }
        return result;
    }
}  