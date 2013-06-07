package com.cannontech.database.db;

/**
 *  Every object that has a DBChangeMsg type associated 
 * with it should implement this interface.
 */

import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

public interface CTIDbChange {
    public abstract DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType);
}
