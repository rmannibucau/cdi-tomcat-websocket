package com.github.rmannibucau.event;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class WebSocketEvent {
    private final WebSocketWithEvent.EventStreamInbound inbound;

    public WebSocketEvent(final WebSocketWithEvent.EventStreamInbound inbound) {
        this.inbound = inbound;
    }

    public void writeBinaryMessage(final byte[] message) {
        try {
            inbound.getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(message));
        } catch (IOException e) {
            throw new WebSocketException(e);
        }
    }

    public void writeTextData(final char message) {
        try {
            inbound.getWsOutbound().writeTextData(message);
        } catch (IOException e) {
            throw new WebSocketException(e);
        }
    }

    public void writeTextMessage(final String message) {
        try {
            inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message.toCharArray()));
        } catch (IOException e) {
            throw new WebSocketException(e);
        }
    }

    public void writeBinaryData(final int b) {
        try {
            inbound.getWsOutbound().writeBinaryData(b);
        } catch (IOException e) {
            throw new WebSocketException(e);
        }
    }

    public void flush() {
        try {
            inbound.getWsOutbound().flush();
        } catch (IOException e) {
            throw new WebSocketException(e);
        }
    }

    public <T> T data(final Class<T> clazz) {
        return clazz.cast(inbound.getData());
    }

    public Object getData() {
        return inbound.getData();
    }

    public void storeData(final Object o) {
        inbound.setData(o);
    }
}
