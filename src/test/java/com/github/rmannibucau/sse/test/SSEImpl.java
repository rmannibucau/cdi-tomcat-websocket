package com.github.rmannibucau.sse.test;

import com.github.rmannibucau.sse.SSEWebSocket;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.Collection;

@WebServlet(urlPatterns = "/impl")
public class SSEImpl extends SSEWebSocket {
    protected void onOpen() {
        System.out.println("opening");
    }

    @Override
    public void sendMessage(final String message) {
        System.out.println("sending message on " + getSSEConnections().size() + " connections");
        super.sendMessage(message);
    }

    protected void onClose(final int status) {
        System.out.println("close: " + status);
    }
}
