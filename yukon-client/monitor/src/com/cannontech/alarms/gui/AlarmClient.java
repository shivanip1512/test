package com.cannontech.alarms.gui;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.messaging.message.dispatch.PointRegistrationMessage;
import com.cannontech.messaging.message.dispatch.RegistrationMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;

/**
 * This type was created in VisualAge.
 */

public class AlarmClient extends com.cannontech.clientutils.ClientBase
{
	/**
	 * TDCClient constructor comment.
	 */
	public AlarmClient( java.util.Observer observingObject ) 
	{
		super( observingObject );
		registerForAlarms();
	}
	
	private void registerForAlarms() {
	    getConnection().write(buildRegistrationMessage());
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	public BaseMessage buildRegistrationMessage() 
	{		
		//First do a registration
		RegistrationMessage reg = new RegistrationMessage();
		reg.setAppName( CtiUtilities.getAppRegistration() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 300 );  // 5 minutes

		//we must do this for all displays now that alarms show on all displays!
		MultiMessage multiReg = new MultiMessage();
		multiReg.getVector().addElement( reg );
		multiReg.getVector().addElement( getPtRegMsg() );
	
		return multiReg;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/20/00 4:16:59 PM)
	 * @return com.cannontech.message.dispatch.message.PointRegistration
	 */
	private PointRegistrationMessage getPtRegMsg() 
	{
		PointRegistrationMessage pReg = new PointRegistrationMessage();
		pReg.setRegFlags( PointRegistrationMessage.REG_ALARMS );

		return pReg;
	}

	/**
	 * Do a no-op implementation for things we do not care about
	 */
	public void receivedDBChangMsg( DBChangeMessage msg ) {}
	public void receivedPointData( PointDataMessage msg ) {}
	public void reRegister( Long[] ptIDs ) {}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/22/00 3:02:24 PM)
	 * @param mpc com.cannontech.message.dispatch.message.Multi
	 */
	public void receivedSignal( SignalMessage signal )
	{
		CTILogger.info(
			"SIGNAL RECEIVED for PtID="+ signal.getPointId() + ",AlarmStateID="+ signal.getCategoryId() + ",Tags(hex)=" + Integer.toHexString(signal.getTags()) +
			",Condition=" + signal.getCondition() );
	}

}
