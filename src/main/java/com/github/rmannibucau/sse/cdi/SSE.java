package com.github.rmannibucau.sse.cdi;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Documented
@Retention(value= RetentionPolicy.RUNTIME)
public @interface SSE {
    String value() default "";
}

