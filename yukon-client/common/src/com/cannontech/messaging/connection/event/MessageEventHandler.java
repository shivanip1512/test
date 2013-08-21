package com.cannontech.messaging.connection.event;

import com.cannontech.event.EventHandler;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.connection.Connection;

public interface MessageEventHandler extends EventHandler {

    void onMessageEvent(Connection connection, Message message);
}
