package com.cannontech.database.db;

/**
 *  Every object that has a DBChangeMsg type associated 
 * with it should implement this interface.
 */

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public interface CTIDbChange {
    public abstract DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType);
}
