package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (6/15/00 2:13:08 PM)
 * @author: 
 */
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;

public interface ClientBaseInterface 
{
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedDBChangMsg( DBChangeMessage msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedPointData( PointDataMessage msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedSignal( SignalMessage msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void reRegister( Long[] ptIDs );
}
