package com.cannontech.common.opc;

import java.util.EventListener;

public interface OpcConnectionListener extends EventListener {
    public void connectionStatusChanged(boolean newStatus);
}
