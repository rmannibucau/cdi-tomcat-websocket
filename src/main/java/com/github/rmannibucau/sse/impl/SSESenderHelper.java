package com.github.rmannibucau.sse.impl;

import com.github.rmannibucau.sse.SSEWebSocket;
import org.apache.catalina.websocket.WebSocketServlet;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SSESenderHelper {
    private static final String WILDCARD = "*";
    private static final String[] EMPTY_URL_PATTERN = new String[0];

    private static Map<ClassLoader, Map<Class<?>, String[]>> URL_PATTERN_CACHE = new ConcurrentHashMap<ClassLoader, Map<Class<?>, String[]>>();

    private SSESenderHelper() {
        // no-op
    }

    public static void sendMessage(final String selectedId, final String msg) {
        final Collection<SSEWebSocket> instances = SSEWebSocketStorage.currentWSs();
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

    private static String[] findUrlPatterns(final SSEWebSocket webSocket) {
        final Class<?> clazz = webSocket.getClass();
        final ClassLoader loader = clazz.getClassLoader();
        final Map<Class<?>, String[]> clValues = getUrlPatternMap(loader);

        String[] patterns = clValues.get(clazz);
        if (patterns != null) {
            return patterns;
        }

        final WebServlet webServlet = webSocket.getClass().getAnnotation(WebServlet.class);
        if (webServlet == null) {
            patterns = EMPTY_URL_PATTERN;
        } else {
            patterns = webServlet.urlPatterns();
        }

        clValues.put(clazz, patterns);
        return patterns;
    }

    private static Map<Class<?>, String[]> getUrlPatternMap(final ClassLoader loader) {
        for (Map.Entry<ClassLoader, Map<Class<?>, String[]>> entry : URL_PATTERN_CACHE.entrySet()) {
            if (classLoaderMatch(loader, entry.getKey())) {
                return entry.getValue();
            }
        }
        return Collections.emptyMap();
    }

    private static boolean classLoaderMatch(final ClassLoader reference, final ClassLoader compared) {
        ClassLoader current = compared;
        do {
            if (current == reference) {
                return true;
            }
            current = current.getParent();
        } while (current != null && !WebSocketServlet.class.getClassLoader().equals(current));
        return false;
    }

    public static void register(final ServletContext ctx) {
        URL_PATTERN_CACHE.put(ctx.getClassLoader(),  new ConcurrentHashMap<Class<?>, String[]>());
    }

    public static void unregister(final ServletContext servletContext) {
        URL_PATTERN_CACHE.remove(servletContext.getClassLoader());
    }
}
