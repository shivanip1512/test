package com.cannontech.alarms.gui;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;

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
	}
	
	/**
	 * This method was created in VisualAge.
	 */
	public Message buildRegistrationMessage() 
	{		
		//First do a registration
		Registration reg = new Registration();
		reg.setAppName( CtiUtilities.getAppRegistration() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 300 );  // 5 minutes

		//we must do this for all displays now that alarms show on all displays!
		Multi multiReg = new Multi();
		multiReg.getVector().addElement( reg );
		multiReg.getVector().addElement( getPtRegMsg() );
	
		return multiReg;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (6/20/00 4:16:59 PM)
	 * @return com.cannontech.message.dispatch.message.PointRegistration
	 */
	private PointRegistration getPtRegMsg() 
	{
		PointRegistration pReg = new PointRegistration();
		pReg.setRegFlags( PointRegistration.REG_ALARMS );

		return pReg;
	}

	/**
	 * Do a no-op implementation for things we do not care about
	 */
	public void receivedDBChangMsg( DBChangeMsg msg ) {}
	public void receivedPointData( PointData msg ) {}
	public void reRegister( Long[] ptIDs ) {}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/22/00 3:02:24 PM)
	 * @param mpc com.cannontech.message.dispatch.message.Multi
	 */
	public void receivedSignal( Signal signal )
	{
		CTILogger.info(
			"SIGNAL RECEIVED for PtID="+ signal.getPointID() + ",AlarmStateID="+ signal.getCategoryID() + ",Tags(hex)=" + Integer.toHexString(signal.getTags()) +
			",Condition=" + signal.getCondition() );
	}

}
