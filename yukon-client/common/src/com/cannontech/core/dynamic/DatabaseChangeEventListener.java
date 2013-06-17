package com.cannontech.core.dynamic;

import com.cannontech.message.dispatch.message.DatabaseChangeEvent;

public interface DatabaseChangeEventListener {
    public void eventReceived(DatabaseChangeEvent event);
}
