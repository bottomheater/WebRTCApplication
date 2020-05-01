package com.cceking.rtc.model;

import com.cceking.rtc.pool.DataConnectionPool;
import com.cceking.rtc.proxy.DataConnectionProxy;
import com.cceking.rtc.proxy.UserConnectionProxy;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;

/**
 * 管理用户的登录和退出
 */
public class DataConnection extends MessageInbound {

    /**
     * 当前连接的用户名称
     */
    private final String userSrc;
    private final String userDst;
    private final String roomId;


    public DataConnection(String userSrc, String userDst, String roomId) {
        this.userSrc = userSrc;
        this.userDst = userDst;
        this.roomId = roomId;
    }

    public static String createId(String userSrc, String userDst) {
        return userSrc + "-" + userDst;
    }

    public String getConnectionId() {
        return createId(this.userSrc, this.userDst);
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        DataConnectionPool dcp = DataConnectionProxy.getDataConnectionPool(roomId);
//        if(dcp.contains(userSrc, userDst)){
//            //向连接池添加当前的连接对象
//            dcp.addMessageInbound(this);
//        }

        //发送历史信息
        UserConnection user = UserConnectionProxy.getUserConnection(roomId, userSrc);
        if (user != null) {
            Map<Integer, ByteBuffer> data = user.getDataQueue().get();
            int len = user.getDataQueue().getLength();
            for (int i = 0; i < len; i++) {
                dcp.sendBinary(userSrc, data.get(i));
            }
        }

    }

    @Override
    protected void onClose(int status) {
        DataConnectionPool dcp = DataConnectionProxy.getDataConnectionPool(roomId);
        // 触发关闭事件，在连接池中移除连接
        dcp.removeMessageInbound(this);
    }

    @Override
    protected void onBinaryMessage(ByteBuffer message) {
        System.out.println(message.mark());
    }

    @Override
    protected void onTextMessage(CharBuffer message) throws IOException {
        System.out.println(message.mark());
    }
}  