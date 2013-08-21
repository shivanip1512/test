package com.cannontech.messaging.util;

import com.cannontech.messaging.connection.ListenerConnection;

public interface ListenerConnectionFactory {
    ListenerConnection createListenerConnection();   
}

