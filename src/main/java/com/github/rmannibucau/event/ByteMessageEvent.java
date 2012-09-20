package com.github.rmannibucau.event;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;

public class ByteMessageEvent extends WebSocketEvent {
    private final byte[] message;

    public ByteMessageEvent(final byte[] b, final WebSocketWithEvent.EventStreamInbound inbound) {
        super(inbound);
        message = b;
    }

    public byte[] getMessage() {
        return message;
    }
}
