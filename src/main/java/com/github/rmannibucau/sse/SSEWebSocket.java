package com.github.rmannibucau.sse;

import com.github.rmannibucau.sse.impl.SSEWebSocketStorage;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class SSEWebSocket extends WebSocketServlet {
    public static final String ID_KEY = "id";

    private final Collection<SSEStreamInbound> sseInbounds = new CopyOnWriteArrayList<SSEStreamInbound>();

    public void init() throws ServletException {
        SSEWebSocketStorage.currentWSs().add(this);
    }

    public void destroy() {
        SSEWebSocketStorage.currentWSs().remove(this);
    }

    @Override
    protected StreamInbound createWebSocketInbound(final String subProtocol, final HttpServletRequest request) {
        return new SSEStreamInbound();
    }

    public void sendMessage(final String message) {
        final Collection<SSEStreamInbound> connections = getSSEConnections();
        for (SSEStreamInbound connection : connections) {
            try {
                final CharBuffer buffer = CharBuffer.wrap(connections.size() + " " + message);
                connection.getWsOutbound().writeTextMessage(buffer);
            } catch (IOException exception) {
                sendError(connection, exception);
            }
        }
    }

    protected void sendError(final SSEStreamInbound connection, final IOException exception) {
        // no-op
    }

    protected void onOpen() {
            // no-op
    }

    protected void onOpen(final SSEStreamInbound streamInbound) {
        // no-op
    }

    protected void onTextMessage(final String msg) {
        throw new UnsupportedOperationException("By default this servlet can't receive any data");
    }

    private void onBinaryMessage(final byte[] bytes) {
        throw new UnsupportedOperationException("By default this servlet can't receive any data");
    }

    protected void onClose(final int status) {
        // no-op
    }

    public Collection<SSEStreamInbound> getSSEConnections() {
        return sseInbounds;
    }

    // doesn't support in, only out messages for SSE
    private class SSEStreamInbound extends MessageInbound {
        @Override
        protected void onBinaryMessage(final ByteBuffer message) throws IOException {
            final byte[] b = new byte[message.remaining()];
            message.get(b);
            SSEWebSocket.this.onBinaryMessage(b);
        }

        @Override
        protected void onTextMessage(final CharBuffer message) throws IOException {
            SSEWebSocket.this.onTextMessage(message.toString());
        }

        @Override
        public void onOpen(final WsOutbound wsOutbound) {
            sseInbounds.add(this);
            SSEWebSocket.this.onOpen();
            SSEWebSocket.this.onOpen(this);
        }

        @Override
        public void onClose(final int status) {
            sseInbounds.remove(this);
            SSEWebSocket.this.onClose(status);
        }
    }
}
