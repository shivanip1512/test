package com.cannontech.clientutils;

/**
 * Insert the type's description here.
 * Creation date: (6/15/00 2:13:08 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;

public interface ClientBaseInterface 
{
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
Message buildRegistrationMessage();
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedDBChangMsg( DBChangeMsg msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedPointData( PointData msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void receivedSignal( Signal msg );
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 2:14:26 PM)
 */
void reRegister( Long[] ptIDs );
}
