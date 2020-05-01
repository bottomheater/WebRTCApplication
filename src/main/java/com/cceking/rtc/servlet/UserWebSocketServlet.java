package com.cceking.rtc.servlet;

import com.cceking.rtc.proxy.UserConnectionProxy;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理用户活动和实时音视频数据，用户前端获取UserConnection实例
 */
@WebServlet(urlPatterns = {"/user"})
public class UserWebSocketServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("websocket chat by " + request.getParameter("uid") + "  " + request.getParameter("rid"));
        super.doGet(request, response);
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest request) {
        String userId = request.getParameter("uid");
        String roomId = request.getParameter("rid");
        roomId = StringUtils.isNotEmpty(roomId) ? roomId : "testRoom";
        return UserConnectionProxy.getUserConnection(roomId, userId);
    }
}
