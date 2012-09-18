package com.github.rmannibucau.sse.cdi.impl;

import com.github.rmannibucau.sse.SSESender;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SenderBean implements Bean<SSESender> {
    private static final Set<Type> TYPES = new HashSet<Type>();
    static {
        TYPES.add(SSESender.class);
    }

    private final SSESender instance;
    private final Set<Annotation> qualifiers = new HashSet<Annotation>();

    public SenderBean(final String id) {
        if (id == null || id.isEmpty()) {
            instance = new SSESender(null);
        } else {
            instance = new SSESender(id);
        }

        qualifiers.add(new SSEAnnotationLiteral(id));
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        return TYPES;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Class<?> getBeanClass() {
        return SSESender.class;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public SSESender create(final CreationalContext<SSESender> context) {
        return instance;
    }

    @Override
    public void destroy(final SSESender instance, final CreationalContext<SSESender> context) {
        // no-op
    }
}
