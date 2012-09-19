package com.github.rmannibucau.sse.impl;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.util.Set;

public class ContextManager implements ServletContainerInitializer, ServletContextListener {
    @Override
    public void onStartup(final Set<Class<?>> classes, final ServletContext ctx) throws ServletException {
        ctx.addListener(this);
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        SSESenderHelper.register(sce.getServletContext());
        SSEWebSocketStorage.register(sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SSESenderHelper.unregister(sce.getServletContext());
        SSEWebSocketStorage.unregister(sce.getServletContext());
    }
}
