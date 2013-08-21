package com.cannontech.messaging.util;

import com.cannontech.messaging.connection.Connection;

public interface ConnectionFactory {
    Connection createConnection();
}
