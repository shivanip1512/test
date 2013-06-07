package com.cannontech.core.dynamic;

import com.cannontech.dispatch.DatabaseChangeEvent;

public interface DatabaseChangeEventListener {
    public void eventReceived(DatabaseChangeEvent event);
}
