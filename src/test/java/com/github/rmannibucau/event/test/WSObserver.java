package com.github.rmannibucau.event.test;

import com.github.rmannibucau.event.OnOpenEvent;

import javax.enterprise.event.Observes;

public class WSObserver {
    public void onOpen(final @Observes OnOpenEvent event) {
        event.writeTextMessage("Open event\r\n");
        event.flush();
        System.out.println("Sent open event");
    }
}
