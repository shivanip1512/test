package com.cannontech.tdc;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This type was created in VisualAge.
 */

//import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.logbox.MessageBoxFrame;


public class TDCClient extends com.cannontech.clientutils.ClientBase
{
	// points we want to register for
	private Set<Integer> pointIDs = null;
	
	private TDCMainPanel caller = null;
	private Display2WayDataAdapter callerModel = null;
	

/**
 * TDCClient constructor comment.
 */
public TDCClient( TDCMainPanel parent, Set<Integer> pointIDs, java.util.Observer observingObject ) 
{
	super( observingObject );
	
	this.caller = parent;
	setPointIDs( pointIDs );
	callerModel = caller.getTableDataModel();
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 12:13:59 PM)
 * @return java.lang.Long[]
 */
private Set<Integer> getPointIDs() {
	return pointIDs;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/00 4:16:59 PM)
 * @return com.cannontech.message.dispatch.message.PointRegistration
 */
private PointRegistration getPtRegMsg() 
{
	PointRegistration pReg = new PointRegistration();
	String displayName = "(null)";
	
	if( caller.getCurrentDisplay() != null )
		displayName = caller.getCurrentDisplay().getName();
		
	//Register for points
	if( Display.isAlarmDisplay(callerModel.getCurrentDisplay().getDisplayNumber()) )
	{
		TDCMainFrame.messageLog.addMessage("Registering display " + displayName + " for ALARMS", MessageBoxFrame.INFORMATION_MSG);
		pReg.setRegFlags( PointRegistration.REG_ALARMS );
	}
	else if( Display.isReadOnlyDisplay(callerModel.getCurrentDisplay().getDisplayNumber()) )
	{
		TDCMainFrame.messageLog.addMessage("Registering display " + displayName + " for ALARMS, EVENTS and NO_UPLOAD", MessageBoxFrame.INFORMATION_MSG);
		pReg.setRegFlags( PointRegistration.REG_EVENTS | 
						  PointRegistration.REG_ALARMS |
			 			  PointRegistration.REG_NO_UPLOAD );
	}
	else // must be a user defined display
	{
		StringBuffer buf = new StringBuffer(
			"Registering display " + displayName + " for ALARMS and Point IDs : ");
				
		if( getPointIDs() != null )
		{
	        buf.append(getPointIDs().stream()
		            .map(id -> id.toString())
		            .collect(Collectors.joining(",")));
			
			pReg = super.getPointRegistration( getPointIDs() );
        }
			
		pReg.setRegFlags( pReg.getRegFlags() | PointRegistration.REG_ALARMS );

		TDCMainFrame.messageLog.addMessage(buf.toString(), MessageBoxFrame.INFORMATION_MSG);
	}

	return pReg;
}

/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedDBChangMsg( DBChangeMsg msg ) 
{
	String display = caller.getCurrentDisplay().getName() != null ? 
			caller.getCurrentDisplay().getName() : "#" + caller.getCurrentDisplay().getDisplayNumber();

	CTILogger.info("DATABASE CHANGE RECEIVED = " + msg.toString() + " Display = " + display );
	
	DefaultDatabaseCache.getInstance().handleDBChangeMessage(
			(com.cannontech.message.dispatch.message.DBChangeMsg)msg);

	//No need to handle any DBChanges that originated from this process (our self)
	if( !(msg.getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE) ) )
		caller.processDBChangeMsg( msg );
}

/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedPointData( PointData point ) 
{
	String display = caller.getCurrentDisplay().getName() != null ? 
			caller.getCurrentDisplay().getName() : "#" + caller.getCurrentDisplay().getDisplayNumber();
			
	CTILogger.info("POINTDATA RECEIVED -- PointID = " + point.getId() +
		" Value = " + point.getValue() + " Tags(hex) = " + Long.toHexString(point.getTags()) +
		" Display = " + display );

	callerModel.processPointDataReceived( point );
}

/**
 * Insert the method's description here.
 * Creation date: (3/22/00 3:02:24 PM)
 * @param mpc com.cannontech.message.dispatch.message.Multi
 */
public void receivedSignal( Signal signal ) 
{
	String display = caller.getCurrentDisplay().getName() != null ? 
			caller.getCurrentDisplay().getName() : "#" + caller.getCurrentDisplay().getDisplayNumber();

	CTILogger.info(
		"SIGNAL RECEIVED for PtID="+ signal.getPointID() + ",AlarmStateID="+ signal.getCategoryID() + ",Tags(hex)=" + Integer.toHexString(signal.getTags()) +
		",Condition=" + signal.getCondition() +
		",Display=" + display );
	
	callerModel.processSignalReceived( signal, caller.getPageNumber() );
}

/**
 * This method was created in VisualAge.
 */
public void reRegister( Set<Integer> pointIDs)
{
	setPointIDs( pointIDs );

	PointRegistration pReg = getPtRegMsg();
	
	super.write( pReg );
}
/**
 * This method was created in VisualAge.
 */
public void reRegisterForNothing()
{
	PointRegistration pReg = new PointRegistration();	
	pReg.setRegFlags( PointRegistration.REG_NOTHING );
	
	super.write( pReg );
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 12:13:59 PM)
 * @param newPointIDArray java.lang.Long[]
 */
private void setPointIDs(Set<Integer> newPointIDs) {
	pointIDs = newPointIDs;
}
}
