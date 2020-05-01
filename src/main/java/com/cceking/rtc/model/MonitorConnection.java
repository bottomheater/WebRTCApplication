package com.cceking.rtc.model;

import com.cceking.rtc.pool.MonitorConnectionPool;
import com.cceking.rtc.proxy.UserConnectionProxy;
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
import java.util.Map;

/**
 * 管理用户的登录和退出
 */
public class MonitorConnection extends MessageInbound {

    /**
     * 当前连接的用户名称
     */
    private final String userId;
    private final String roomId;


    public MonitorConnection(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        MonitorConnectionPool.addMonitorConnection(this);
        UserConnection uc = UserConnectionProxy.getUserConnection(roomId, userId);
        Map<Integer, ByteBuffer> data = uc.getDataQueue().get();
        int len = uc.getDataQueue().getLength();
        for (int i = 0; i < len; i++) {
            MonitorConnectionPool.sendBinaryToUser(MonitorConnectionPool.createId(roomId, userId), data.get(i));
        }
    }

    @Override
    protected void onClose(int status) {
        MonitorConnectionPool.removeMonitorConnection(this);
    }

    @Override
    protected void onBinaryMessage(ByteBuffer message) {
        System.out.println(message.mark());
    }

    @Override
    protected void onTextMessage(CharBuffer message) throws IOException {
        System.out.println(message.mark());
        if ("merge".contentEquals(message)) {
            UserConnection uc = UserConnectionProxy.getUserConnection(roomId, userId);
            Map<Integer, ByteBuffer> data = uc.getDataQueue().get();
            int len = uc.getDataQueue().getLength();
            Path dir = Paths.get("/usr/video");
            if (Files.notExists(dir)) {
                try {
                    Files.createDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final Path copy_to = dir.resolve(userId + "chat.webm");
            if (Files.notExists(copy_to)) {
                try {
                    Files.createFile(copy_to);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileChannel fileChannel_to = null;
            try {
                fileChannel_to = (FileChannel.open(copy_to,
                        EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)));
                for (int i = 0; i < len; i++) {
                    System.out.println(data.get(i).mark());
                    fileChannel_to.write(data.get(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String createId(String roomId, String userId) {
        return roomId + "-" + userId;
    }

    public String getId() {
        return roomId + "-" + userId;
    }
}  