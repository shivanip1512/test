package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (8/30/00 9:43:39 AM)
 * @author: 
 */
import java.util.ArrayList;
import java.util.List;

import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.tdc.roweditor.SendData;

public class ControlCommand 
{
	/* Status Point State definitions */
	// 100900 CGP States changed to 0 and 1
	public static final int CONTROL_INVALID = -1;
	public static final int CONTROL_OPENED = 0;
	public static final int CONTROL_CLOSED = 1;
	public static final int CONTROL_INDETERMINATE = 2;
	
	public static final int CONTROL_STATEZERO = 0;
	public static final int CONTROL_STATEONE = 1;
	public static final int CONTROL_STATETWO = 2;
	public static final int CONTROL_STATETHREE = 3;
	public static final int CONTROL_STATEFOUR = 4;
	public static final int CONTROL_STATEFIVE = 5;
	public static final int CONTROL_STATESIX = 6;
/**
 * ControlCommand constructor comment.
 */
public ControlCommand() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/00 5:32:43 PM)
 * Version: <version>
 */
public static void send( long deviceID, long pointID, int rawState ) 
{
	/*** Start building the Command.opArgList() **************************/
	List<Integer> data = new ArrayList<Integer>(4);

	data.add(-1);  // this is the ClientRegistrationToken

	if( deviceID > 0 ) {
		data.add((int)deviceID);
    }
	else {
		data.add(0);
    }
		
	data.add((int)pointID);
	data.add(rawState);
	/*** End building the Command.opArgList() ****************************/

	CommandMessage cmd = new CommandMessage();
	cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	cmd.setOperation( CommandMessage.CONTROL_REQUEST );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	SendData.getInstance().sendCommandMsg( cmd );	
}
}
