package com.cannontech.database.db;

/**
 * Insert the type's description here.
 * Creation date: (9/12/2001 9:57:13 AM)
 * @author: 
 */
// Every object that has a DBChangeMsg type associated 
//  with it should implement this interface.
import com.cannontech.message.dispatch.message.DBChangeMsg;

public interface CTIDbChange {
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 9:57:50 AM)
 * @return com.cannontech.message.dispatch.message.DBChangeMsg
 */
DBChangeMsg[] getDBChangeMsgs( int typeOfChange );
}
