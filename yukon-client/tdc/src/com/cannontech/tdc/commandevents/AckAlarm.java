package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (4/10/00 5:31:59 PM)
 * @author: 
 * @Version: <version>
 */
import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.util.Command;
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
public static void sendAckAll( int pointid ) 
{
    List<Integer> data = new ArrayList<Integer>(2);
	data.add(-1);  // this is the ClientRegistrationToken
	
	//add the pointID
	data.add(pointid);
	//add the ACK_ALL reserved value instead of the AlarmCondition
	data.add(Command.ACK_ALL_TOKEN);

		
	// Sends a vangogh command message to capcontrol, which then forwards the exact
	//   message onto dispatch(vangogh)
	Command cmd = new Command();
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
public static void send( int[] pointids, int[] conditions ) 
{
	if( pointids.length != conditions.length )
		throw new IllegalStateException("The pointIDS and AlarmConditions must be a one to one mapping");
		
		
    List<Integer> data = new ArrayList<Integer>(pointids.length+1);
	data.add(-1);  // this is the ClientRegistrationToken
	
	for( int i = 0 ; i < pointids.length; i++ ) {
		data.add(pointids[i]);
		data.add(conditions[i]);
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
public static void send( int pointid, int condition ) 
{
	int[] ints = new int[1];
	ints[0] = pointid;

	int[] conditions = new int[1];
	conditions[0] = condition;
	
	send( ints, conditions );
}
}
