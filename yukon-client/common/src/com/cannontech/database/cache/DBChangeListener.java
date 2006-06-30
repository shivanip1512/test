package com.cannontech.database.cache;

import com.cannontech.message.dispatch.message.DBChangeMsg;

public interface DBChangeListener {
    public void dbChangeReceived(DBChangeMsg dbChange);
}
