package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (8/30/00 9:43:39 AM)
 * @author: 
 */
import com.cannontech.tdc.roweditor.SendData;
import com.cannontech.message.dispatch.message.Command;
import java.util.Vector;

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
	Vector data = new Vector( 4 );

	data.addElement( new Integer(-1) );  // this is the ClientRegistrationToken

	if( deviceID > 0 )
		data.addElement( new Integer((int)deviceID));
	else
		data.addElement( new Integer(0) );	
		
	data.addElement( new Integer((int)pointID) );
	data.addElement( new Integer(rawState) );
	/*** End building the Command.opArgList() ****************************/

	Command cmd = new Command();
	cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	cmd.setOperation( Command.CONTROL_REQUEST );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	SendData.getInstance().sendCommandMsg( cmd );	
}
}
