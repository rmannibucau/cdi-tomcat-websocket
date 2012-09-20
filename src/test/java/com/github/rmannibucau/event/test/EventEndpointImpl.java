package com.github.rmannibucau.event.test;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;
import com.github.rmannibucau.sse.SSEWebSocket;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/websocket")
public class EventEndpointImpl extends WebSocketWithEvent {
}
