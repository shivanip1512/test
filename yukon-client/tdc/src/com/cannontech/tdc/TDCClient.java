package com.cannontech.tdc;

/**
 * This type was created in VisualAge.
 */

//import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;


public class TDCClient extends com.cannontech.clientutils.ClientBase
{
	// points we want to register for
	private Long[] pointIDArray = null;
	
	private TDCMainPanel caller = null;
	private Display2WayDataAdapter callerModel = null;
	

/**
 * TDCClient constructor comment.
 */
public TDCClient( TDCMainPanel parent, Long[] pointIDs, java.util.Observer observingObject ) 
{
	super( observingObject );
	
	this.caller = parent;
	setPointIDArray( pointIDs );
	callerModel = caller.getTableDataModel();
}
/**
 * This method was created in VisualAge.
 */
public Message buildRegistrationMessage() 
{		
	//First do a registration
	Registration reg = new Registration();
	reg.setAppName("TDC_RECEIVER@" + com.cannontech.common.util.CtiUtilities.getUserName() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 10 );  // 5 minutes

	//we must do this for all displays now that alarms show on all displays!
	Multi multiReg = new Multi();
	multiReg.getVector().addElement( reg );
	multiReg.getVector().addElement( getPtRegMsg() );

	return multiReg;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/2001 12:13:59 PM)
 * @return java.lang.Long[]
 */
private java.lang.Long[] getPointIDArray() {
	return pointIDArray;
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
				
		if( getPointIDArray() != null )
		{
			for( int i = 0; i < getPointIDArray().length; i++ )
				buf.append( getPointIDArray()[i].toString() + ",");
				
			pReg = super.getPointRegistration( getPointIDArray() );
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
	
	callerModel.processSignalReceived( signal );
}

/**
 * This method was created in VisualAge.
 */
public void reRegister( Long[] pointIDs)
{
	setPointIDArray( pointIDs );

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
private void setPointIDArray(java.lang.Long[] newPointIDArray) {
	pointIDArray = newPointIDArray;
}
}
