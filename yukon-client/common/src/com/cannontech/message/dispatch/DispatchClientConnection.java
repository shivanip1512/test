package com.cannontech.message.dispatch;

import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.message.util.ClientConnection;

@ManagedResource
public class DispatchClientConnection extends ClientConnection {

    public DispatchClientConnection() {
        super("Dispatch");
    }

}
