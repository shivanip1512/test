package com.cannontech.message.dispatch.test;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.message.dispatch.*;

public class DBChangeSource {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	if( args.length != 4 )
	{
		System.out.println("Usage:  DBChangeSource vangoghmachine port numberofchanges delay");
		System.out.println("specify numberofchanges = -1 to keep sending changes forever");
		System.out.println("note that port 1510 has been the default");
		System.exit(0);
	}

	String vanGogh = args[0];
	int port = (Integer.decode(args[1])).intValue();
	int numChanges = (Integer.decode(args[2])).intValue();
	int delay = (Integer.decode(args[3])).intValue();

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
		e.printStackTrace();
		System.exit(0);
	}

	//First do a registration
	System.out.println("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Datbase Change Source - Java" + (new java.util.Date()).getTime() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );

	conn.write( reg );

	//Do a loopback
	System.out.println("Attempting a loopback command");
	com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
	cmd.setOperation( com.cannontech.message.dispatch.message.Command.NO_OP );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	System.out.println("Received loopback:  " + response );

	//Send changes
	int numSent = 0;
	
	while( forever || numSent < numChanges )
	{
		//com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg();
		//dbChange.setDatabase(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ALL_DB);
		//dbChange.setDBType(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD);
		//conn.write( dbChange );
		numSent++;
		System.out.println("Sent change #" + numSent);
		try
		{
			Thread.sleep(delay);
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	System.exit(0);

}
}
