package com.github.rmannibucau.event;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;

public class TextMessageEvent extends WebSocketEvent {
    private final String message;

    public TextMessageEvent(final String msg, final WebSocketWithEvent.EventStreamInbound inbound) {
        super(inbound);
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}
