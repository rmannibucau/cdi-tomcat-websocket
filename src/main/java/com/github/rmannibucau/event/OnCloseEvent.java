package com.github.rmannibucau.event;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;

public class OnCloseEvent extends WebSocketEvent {
    private final int status;

    public OnCloseEvent(final WebSocketWithEvent.EventStreamInbound inbound, final int status) {
        super(inbound);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
