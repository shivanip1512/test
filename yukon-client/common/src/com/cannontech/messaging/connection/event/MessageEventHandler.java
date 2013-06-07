package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventHandler;
import com.cannontech.messaging.connection.Connection;
import com.cannontech.messaging.message.BaseMessage;

public interface MessageEventHandler extends EventHandler {

    void onMessageEvent(Connection connection, BaseMessage message);
}
