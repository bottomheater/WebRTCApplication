package com.cceking.rtc.servlet;

import com.cceking.rtc.model.MonitorConnection;
import com.cceking.rtc.pool.MonitorConnectionPool;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/monitor"})
public class MonitorWebSocketServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uid = request.getParameter("uid");
        String roomId = request.getParameter("rid");
        System.out.println("data connection uid " + uid + " by " + roomId);
        super.doGet(request, response);
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest request) {
        String uid = request.getParameter("uid");
        String roomId = request.getParameter("rid");
        roomId = StringUtils.isNotEmpty(roomId) ? roomId : "roomId";
        uid = StringUtils.isNotEmpty(uid) ? uid : "uid";
        MonitorConnection d = MonitorConnectionPool.getMonitorConnection(roomId, uid);
        return d;
    }
}
