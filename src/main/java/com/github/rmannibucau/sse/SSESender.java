package com.github.rmannibucau.sse;

import com.github.rmannibucau.sse.impl.SSESenderHelper;

public class SSESender {
    private final String id;

    protected SSESender() { // used by proxying
        id = null;
    }

    public SSESender(final String selectedid) {
        if (selectedid == null || selectedid.isEmpty()) {
            id = null;
        } else {
            id = selectedid;
        }
    }

    public void sendMessage(final String msg) {
        SSESenderHelper.sendMessage(id, msg);
    }
}
