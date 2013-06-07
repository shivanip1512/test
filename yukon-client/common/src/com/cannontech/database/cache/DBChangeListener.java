package com.cannontech.database.cache;

import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public interface DBChangeListener {
    public void dbChangeReceived(DBChangeMessage dbChange);
}
