package com.cceking.rtc.servlet;

import com.cceking.rtc.model.DataConnection;
import com.cceking.rtc.proxy.DataConnectionProxy;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/data"})
public class DataWebSocketServlet extends WebSocketServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String roomId = request.getParameter("rid");
        System.out.println("data connection from " + from + " to " + to + " by " + roomId);
        super.doGet(request, response);
    }

    @Override
    protected StreamInbound createWebSocketInbound(String s, HttpServletRequest request) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String roomId = request.getParameter("rid");
        roomId = StringUtils.isNotEmpty(roomId) ? roomId : "testRoom";
        DataConnection d = DataConnectionProxy.getDataConnection(roomId, from, to);
        return d;
    }
}
