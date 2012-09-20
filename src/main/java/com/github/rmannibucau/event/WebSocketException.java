package com.github.rmannibucau.event;

import java.io.IOException;

public class WebSocketException extends RuntimeException {
    public WebSocketException(final IOException e) {
        super(e);
    }
}
