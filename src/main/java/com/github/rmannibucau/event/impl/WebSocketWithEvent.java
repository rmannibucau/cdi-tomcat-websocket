package com.github.rmannibucau.event.impl;

import com.github.rmannibucau.event.ByteMessageEvent;
import com.github.rmannibucau.event.OnCloseEvent;
import com.github.rmannibucau.event.OnOpenEvent;
import com.github.rmannibucau.event.TextMessageEvent;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketWithEvent extends WebSocketServlet {
    @Inject
    private BeanManager beanManager;

    @Override
    protected StreamInbound createWebSocketInbound(final String subProtocol, final HttpServletRequest request) {
        return new EventStreamInbound();
    }

    public class EventStreamInbound extends MessageInbound {
        private final AtomicReference<Object> data = new AtomicReference<Object>();

        @Override
        protected void onBinaryMessage(final ByteBuffer message) throws IOException {
            final byte[] b = new byte[message.remaining()];
            message.get(b);
            beanManager.fireEvent(new ByteMessageEvent(b, this));
        }

        @Override
        protected void onTextMessage(final CharBuffer message) throws IOException {
            beanManager.fireEvent(new TextMessageEvent(message.toString(), this));
        }

        @Override
        public void onOpen(final WsOutbound wsOutbound) {
            beanManager.fireEvent(new OnOpenEvent(this));
        }

        @Override
        public void onClose(final int status) {
            beanManager.fireEvent(new OnCloseEvent(this, status));
        }

        public Object getData() {
            return data.get();
        }

        public void setData(final Object o) {
            data.set(o);
        }
    }
}
