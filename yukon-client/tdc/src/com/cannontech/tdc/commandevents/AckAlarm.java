package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (4/10/00 5:31:59 PM)
 * @author: 
 * @Version: <version>
 */
import java.util.Vector;

import com.cannontech.message.dispatch.message.Command;
import com.cannontech.tdc.roweditor.SendData;

public class AckAlarm 
{
/**
 * AckAlarm constructor comment.
 */
public AckAlarm() 
{
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
	cmd.setOperation( Command.ACKNOWLEGDE_ALARM );
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
