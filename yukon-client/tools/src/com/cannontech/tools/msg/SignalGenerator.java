package com.cannontech.tools.msg;

/**
 * Insert the type's description here.
 * Creation date: (6/13/00 10:50:34 AM)
 * @author: 
 */
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.dispatch.ClientConnection;

public class SignalGenerator {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	if( args.length < 5 )
	{
		com.cannontech.clientutils.CTILogger.info("Usage:  SignalGenerator vangoghmachine port numberofchanges delay pointcount { pointID }");
		com.cannontech.clientutils.CTILogger.info("specify numberofchanges = -1 to keep sending changes forever");
		com.cannontech.clientutils.CTILogger.info("note that port 1510 has been the default");
		System.exit(0);
	}

	String vanGogh = args[0];
	int port = (Integer.decode(args[1])).intValue();
	int numChanges = (Integer.decode(args[2])).intValue();
	int delay = (Integer.decode(args[3])).intValue();
	int pointCount = (Integer.decode(args[4])).intValue();
	
	int[] id = new int[ pointCount ];

	for( int i = 0; i < id.length; i++ )
	{
		id[i] = (Integer.decode( args[5+i] )).intValue();
	}

	boolean forever = false;

	if( numChanges == -1 )
	 	forever = true;
	
	ClientConnection conn = new ClientConnection();

	conn.setHost(vanGogh);
	conn.setPort(port);

	try
	{
		conn.connect();
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		System.exit(0);
	}

	//First do a registration
	com.cannontech.clientutils.CTILogger.info("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName( CtiUtilities.getAppRegistration() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 300 );

	conn.write( reg );

	//Do a loopback
	com.cannontech.clientutils.CTILogger.info("Attempting a loopback command");
	com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
	cmd.setOperation( com.cannontech.message.dispatch.message.Command.LOOP_CLIENT );
	cmd.setTimeStamp( new java.util.Date() );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	com.cannontech.clientutils.CTILogger.info("Received loopback:  " + response );
	
	//Send changes
	int numSent = 0;

	while( forever || numSent < numChanges )
	{
		for( int j = 0; j < id.length; j++ )
		{				
			com.cannontech.message.dispatch.message.Signal signal = new com.cannontech.message.dispatch.message.Signal();

//			signal.setLogPriority((int)(Math.round(Math.random() * 6.0)+1)); // log between 1-7
			signal.setCategoryID(2); // log between 1==EVENT,  2-11==ALARM
			signal.setTags( com.cannontech.message.dispatch.message.Signal.MASK_ANY_ALARM | com.cannontech.message.dispatch.message.Signal.MASK_ANY_SERVICE_DISABLE);
//			signal.setTags(0);
			
			//signal.setDescription("GROUP: LCR Test Group r1 r2");
			//signal.setAction("SHED 4H");
			signal.setDescription("Description: ???"); //if null, we crash
			signal.setAction("Action: ???"); //if null, we crash
			
			signal.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());

			signal.setPointID( id[j] );

			conn.write( signal );  // write the signal
		}

		numSent++;
			
		com.cannontech.clientutils.CTILogger.info((new java.util.Date()).toString() + " - Sent change #" + numSent);
		try
		{
			Thread.sleep(delay);
		}
		catch( InterruptedException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}

	System.exit(0);
}
}
