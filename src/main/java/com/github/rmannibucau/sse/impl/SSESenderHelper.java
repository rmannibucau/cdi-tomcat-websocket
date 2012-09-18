package com.github.rmannibucau.sse.impl;

import com.github.rmannibucau.sse.SSEWebSocket;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class SSESenderHelper {
    private static Map<ClassLoader, ServletContext> CONTEXTS = new HashMap<ClassLoader, ServletContext>();
    public static final String WILDCARD = "*";

    private SSESenderHelper() {
        // no-op
    }

    public static void sendMessage(final String selectedId, final String msg) {
        final ServletContext servletContext = currentContext();
        if (servletContext == null) {
            return;
        }

        final Collection<SSEWebSocket> instances = (Collection<SSEWebSocket>) servletContext.getAttribute(SSEWebSocket.INSTANCES_KEY);
        if (instances == null) {
            return;
        }

        for (SSEWebSocket webSocket : instances) {
            String id = webSocket.getInitParameter(SSEWebSocket.ID_KEY);
            if (id == null) {
                final String[] urlPatterns = findUrlPatterns(webSocket);
                if (urlPatterns != null) {
                    for (String pattern : urlPatterns) {
                        if (match(selectedId, pattern)) {
                            webSocket.sendMessage(msg);
                            break;
                        }
                    }
                }
            } else if (match(selectedId, id)) {
                webSocket.sendMessage(msg);
            }
        }
    }

    private static boolean match(final String selectedId, final String id) {
        if (selectedId == null) {
            return true;
        }

        if (selectedId.endsWith(WILDCARD) && id != null) {
            return id.startsWith(selectedId.substring(0, selectedId.length() - 1));
        }
        return selectedId.equals(id);
    }

    private static String[] findUrlPatterns(final SSEWebSocket webSocket) { // TODO: cache
        final WebServlet webServlet = webSocket.getClass().getAnnotation(WebServlet.class);
        if (webServlet == null) {
            return null;
        }

        return webServlet.urlPatterns();
    }

    private static ServletContext currentContext() {
        return CONTEXTS.get(Thread.currentThread().getContextClassLoader());
    }

    public static void register(final ServletContext ctx) {
        CONTEXTS.put(ctx.getClassLoader(), ctx);
    }

    public static void unregister(final ServletContext servletContext) {
        CONTEXTS.remove(servletContext.getClassLoader());
    }
}
