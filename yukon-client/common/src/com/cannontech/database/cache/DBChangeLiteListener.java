package com.cannontech.database.cache;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;


/**
 * You can implement this interface if you'd like to receive database change
 * messages.  Currently you have to register DBChangeLiteListener instances with
 * the cache.  Most apps don't need to use this.  
 * 
 * Creation date: (12/20/2001 1:10:41 PM)
 * @author: 
 */
public interface DBChangeLiteListener {

/**
 * Called when a database change occurs.  If it can be determined,
 * an object representing the change will be passed as the lBase parameter.
 * Creation date: (12/20/2001 1:42:07 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 * @param lBase An object representing what changed, could be null
 */
void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase );
}
