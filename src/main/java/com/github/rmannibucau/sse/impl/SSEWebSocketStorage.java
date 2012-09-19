package com.github.rmannibucau.sse.impl;

import com.github.rmannibucau.sse.SSEWebSocket;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SSEWebSocketStorage {
    private static Map<ClassLoader, Collection<SSEWebSocket>> WS_BY_CLASSLOADER = new HashMap<ClassLoader, Collection<SSEWebSocket>>();

    private SSEWebSocketStorage() {
        // no-op
    }

    public static Collection<SSEWebSocket> currentWSs() {
        return WS_BY_CLASSLOADER.get(Thread.currentThread().getContextClassLoader());
    }

    public static void register(final ServletContext ctx) {
        WS_BY_CLASSLOADER.put(ctx.getClassLoader(), new CopyOnWriteArrayList<SSEWebSocket>());
    }

    public static void unregister(final ServletContext servletContext) {
        WS_BY_CLASSLOADER.remove(servletContext.getClassLoader());
    }
}
