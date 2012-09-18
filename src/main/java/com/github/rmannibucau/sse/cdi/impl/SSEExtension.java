package com.github.rmannibucau.sse.cdi.impl;

import com.github.rmannibucau.sse.cdi.SSE;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.HashSet;
import java.util.Set;

public class SSEExtension implements Extension {
    private final Set<String> ids = new HashSet<String>();

    protected <T> void addSSEInjections(@Observes final ProcessAnnotatedType<T> pat) {
        for (AnnotatedField<?> field : pat.getAnnotatedType().getFields()) {
            final SSE sse = field.getAnnotation(SSE.class);
            if (sse != null) {
                ids.add(sse.value());
            }
        }
    }

    protected void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
        for (String id : ids) {
            abd.addBean(new SenderBean(id));
        }
        ids.clear();
    }
}
