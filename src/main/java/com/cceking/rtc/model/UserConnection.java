package com.cceking.rtc.model;

import com.cceking.rtc.pool.DataConnectionPool;
import com.cceking.rtc.pool.MonitorConnectionPool;
import com.cceking.rtc.pool.UserConnectionPool;
import com.cceking.rtc.proxy.DataConnectionProxy;
import com.cceking.rtc.proxy.UserConnectionProxy;
import net.sf.json.JSONObject;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

/**
 * 管理用户的登录和退出，上传的视频数据
 */
public class UserConnection extends MessageInbound {

    /**
     * 当前连接的用户名称
     */
    private final String user;
    /**
     * 当前连接的用户视频数据
     */
    private final DataQueue dataQueue;
    /**
     * 房间ID，可看作群聊ID
     */
    private String roomId;
    /**
     * 停止接收发送端数据的标志
     */
    private boolean isStop = false;
    /**
     * 保存视频到服务器本地上
     */
    private boolean isSave = false;
    /**
     * 视频文件入口
     */
    FileChannel fileChannel_to = null;


    public UserConnection(String roomId, String user) {
        this.roomId = roomId;
        this.user = user;
        this.dataQueue = new DataQueue();

        Path dir = Paths.get("/usr/video");
        if (Files.notExists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final Path copy_to = dir.resolve(user + "chat.webm");
        if (Files.notExists(copy_to)) {
            try {
                Files.createFile(copy_to);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //copy_to.toFile().delete();
            fileChannel_to = (FileChannel.open(copy_to,
                    EnumSet.of(StandardOpenOption.APPEND, StandardOpenOption.WRITE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return this.user;
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        UserConnectionPool ucp = UserConnectionProxy.getUserConnectionPool(roomId);
        //向连接池添加当前的连接对象
        //ucp.addMessageInbound(this);


        //向所有在线用户推送当前用户上线的消息
        JSONObject result = new JSONObject();
        result.element("type", "user_login");
        result.element("user", this.user);
        ucp.sendMessage(result.toString());


        //向当前连接发送当前在线用户的列表
        result = new JSONObject();
        result.element("type", "get_online_user");
        result.element("list", ucp.getOnlineUser());
        ucp.sendMessageToUser(this.user, result.toString());
    }

    @Override
    protected void onClose(int status) {
        UserConnectionPool ucp = UserConnectionProxy.getUserConnectionPool(roomId);
        // 触发关闭事件，在连接池中移除连接
        ucp.removeMessageInbound(this);

        //向在线用户发送当前用户退出的消息
        JSONObject result = new JSONObject();
        result.element("type", "user_logout");
        result.element("status", status);
        result.element("user", this.user);
        ucp.sendMessage(result.toString());
    }

    @Override
    protected void onBinaryMessage(ByteBuffer message) {
        DataConnectionPool dcp = DataConnectionProxy.getDataConnectionPool(roomId);
        //System.out.println(dcp.toString()+message.mark());

        //接收视频数据
        if (!isStop) {
            //获取有效数据(父类ByteBuffer复用)
            byte[] b = new byte[message.limit()];
            message.get(b);
            ByteBuffer wrap = ByteBuffer.wrap(b);
            dataQueue.append(wrap);

            //保存到本地
            if (isSave) {
                try {
                    fileChannel_to.write(wrap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            dcp.sendBinary(user, message);

            //发送到监控台
            MonitorConnectionPool.sendBinaryToUser(MonitorConnectionPool.createId(roomId, user), message);
            System.out.println(roomId + " stop " + user);
        }
    }

    @Override
    protected void onTextMessage(CharBuffer message) throws IOException {
        System.out.println(message.toString());

        //接收文本信息并推送到同组用户
        DataConnectionPool dcp = DataConnectionProxy.getDataConnectionPool(roomId);
        dcp.sendText(user, message);
    }

    public void clear() {
        dataQueue.clear();
    }

    public DataQueue getDataQueue() {
        return dataQueue;
    }

    public void stop() {
        isStop = true;
    }

    public void active() {
        isStop = false;
    }

    public String getUserId() {
        return user;
    }

    public String getRoomId() {
        return roomId;
    }
}  