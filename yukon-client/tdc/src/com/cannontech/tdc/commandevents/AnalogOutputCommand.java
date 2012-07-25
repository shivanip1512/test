package com.cannontech.tdc.commandevents;

/**
 * Insert the type's description here.
 * Creation date: (8/30/00 9:43:39 AM)
 * @author:
 */
import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.util.Command;
import com.cannontech.tdc.roweditor.SendData;

public class AnalogOutputCommand
{
/**
 * Insert the method's description here.
 * Creation date: (4/10/00 5:32:43 PM)
 * Version: <version>
 */
public static void send( long pointID, double outputValue )
{
	/*** Start building the Command.opArgList() **************************/
	List<Integer> data = new ArrayList<Integer>(4);

	data.add((int)pointID);
	data.add((int)outputValue);
	/*** End building the Command.opArgList() ****************************/

	Command cmd = new Command();
	cmd.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
	cmd.setOperation( Command.ANALOG_OUTPUT_REQUEST );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new java.util.Date() );

	SendData.getInstance().sendCommandMsg( cmd );
}
}
