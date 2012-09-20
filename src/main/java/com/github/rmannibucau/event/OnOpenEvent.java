package com.github.rmannibucau.event;

import com.github.rmannibucau.event.impl.WebSocketWithEvent;

public class OnOpenEvent extends WebSocketEvent {
    public OnOpenEvent(final WebSocketWithEvent.EventStreamInbound inbound) {
        super(inbound);
    }
}
