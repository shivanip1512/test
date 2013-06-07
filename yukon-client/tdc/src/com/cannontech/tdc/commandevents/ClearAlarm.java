package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (4/11/00 9:21:49 AM)
 * @author: 
 * @Version: <version>
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.tdc.roweditor.SendData;

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
public static void send( int[] pointids, int[] conditions ) 
{
	if( pointids.length != conditions.length )
		throw new IllegalStateException("The pointIDS and AlarmConditions must be a one to one mapping");
		
		
    List<Integer> data = new ArrayList<Integer>(pointids.length+1);
	data.add(-1);  // this is the ClientRegistrationToken
	
	for( int i = 0 ; i < pointids.length; i++ )
	{
		data.add(pointids[i]);
		data.add(conditions[i]);
	}

	CommandMessage cmd = new CommandMessage();
	cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	cmd.setOperation( CommandMessage.CLEAR_ALARM );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	SendData.getInstance().sendCommandMsg( cmd );
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/00 5:32:43 PM)
 * Version: <version>
 */
public static void send( int pointid, int condition ) 
{
	int[] ints = new int[1];
	ints[0] = pointid;

	int[] conditions = new int[1];
	conditions[0] = condition;
	
	send( ints, conditions );
}
}
