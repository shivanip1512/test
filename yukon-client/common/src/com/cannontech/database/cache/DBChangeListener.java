package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (12/20/2001 1:10:41 PM)
 * @author: 
 */
public interface DBChangeListener {
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:42:07 PM)
 */
com.cannontech.message.util.ClientConnection getClientConnection();
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:42:07 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 */
void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase lBase );
}
