package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (4/11/00 9:21:49 AM)
 * @author: 
 * @Version: <version>
 */
import com.cannontech.tdc.roweditor.SendData;
import com.cannontech.message.dispatch.message.Command;
import java.util.Vector;

public class ClearAlarm 
{
/**
 * ClearAlarm constructor comment.
 */
public ClearAlarm() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/00 5:32:43 PM)
 * Version: <version>
 */
public static void send( int[] pointids ) 
{
	Vector data = new Vector( pointids.length + 1 );
	data.addElement( new Integer(-1) );  // this is the ClientRegistrationToken
	
	for( int i = 0 ; i < pointids.length; i++ )
	{
		data.addElement( new Integer(pointids[i]) );
	}

	Command cmd = new Command();
	cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	cmd.setOperation( Command.CLEAR_ALARM );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	SendData.getInstance().sendCommandMsg( cmd );
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/00 5:32:43 PM)
 * Version: <version>
 */
public static void send( int pointid ) 
{
	int[] ints = new int[1];
	ints[0] = pointid;
	
	send( ints );	
}
}
