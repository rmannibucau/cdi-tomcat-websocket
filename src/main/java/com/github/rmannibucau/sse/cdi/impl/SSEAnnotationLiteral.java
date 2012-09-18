package com.github.rmannibucau.sse.cdi.impl;

import com.github.rmannibucau.sse.cdi.SSE;

import javax.enterprise.util.AnnotationLiteral;

public class SSEAnnotationLiteral extends AnnotationLiteral<SSE> implements SSE {
    private final String id;

    public SSEAnnotationLiteral(final String value) {
        id = value;
    }

    @Override
    public String value() {
        return id;
    }
}
