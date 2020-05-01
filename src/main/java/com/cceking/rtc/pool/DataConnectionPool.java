package com.cceking.rtc.pool;

import com.cceking.rtc.model.DataConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataConnectionPool {

    private String roomId = "chat";

    //保存连接的MAP容器  
    private final Map<String, DataConnection> connections = new HashMap<>();

    public DataConnectionPool(String roomId){
        this.roomId = roomId;
    }

    //向连接池中添加连接
    public void addMessageInbound(DataConnection inbound){
        //添加连接
        System.out.println("data : " + inbound.getConnectionId() + " login..");
        connections.put(inbound.getConnectionId(), inbound);
    }

    //获取所有的在线用户
    public Set<String> getOnlineUser(){
        return connections.keySet();
    }

    public boolean contains(String userSrc, String userDst){
        return connections.containsKey(DataConnection.createId(userSrc, userDst));
    }

    public DataConnection get(String userSrc, String userDst){
        return connections.get(DataConnection.createId(userSrc, userDst));
    }

    public void removeMessageInbound(DataConnection inbound){
        //移除连接
        System.out.println("data : " + inbound.getConnectionId() + " disconnect..");
        connections.remove(inbound.getConnectionId());
    }

    //向特定的用户发送数据
    public void sendMessageToUser(String user,String message){
        try {
            System.out.println("send message to user : " + user + " ,message content : " + message);
            DataConnection inbound = connections.get(user);
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
                DataConnection inbound = connections.get(key);
                if(inbound != null){
                    System.out.println("send message to user : " + key + " ,message content : " + message);
                    inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBinaryToUser(String user, ByteBuffer message){
        try {
            //向特定的用户发送数据
            System.out.println("send message to user : " + user + " ,message content : " + message);
            DataConnection inbound = connections.get(user);
            if(inbound != null){
                inbound.getWsOutbound().writeBinaryMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向所有的用户发送二进制流
    public void sendBinary(ByteBuffer message){
        try {
            Set<String> keySet = connections.keySet();
            for (String key : keySet) {
                DataConnection inbound = connections.get(key);
                if(inbound != null){
                    System.out.println("send message to user : " + key + " ,message content : " + message);
                    inbound.getWsOutbound().writeBinaryMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送相同包到相同源的
    public void sendBinary(String src, ByteBuffer message){
        try {
            Set<String> keySet = connections.keySet();
            for (String key : keySet) {
                if( key.contains(src+"-") ){
                    DataConnection inbound = connections.get(key);
                    if(inbound != null){
                        System.out.println("send binary to user : " + key + " ,message content : " + message);
                        inbound.getWsOutbound().writeBinaryMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //发送相同包到相同源的
    public void sendText(String src, CharBuffer message){
        try {
            Set<String> keySet = connections.keySet();
            for (String key : keySet) {
                if( key.contains(src+"-") ){
                    DataConnection inbound = connections.get(key);
                    if(inbound != null){
                        System.out.println("send text to user : " + key + " ,message content : " + message);
                        inbound.getWsOutbound().writeTextMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  