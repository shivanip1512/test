package com.cannontech.dispatch.test;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.dispatch.DispatchClientConnection;
import com.cannontech.messaging.message.CommandMessage;

public class ChangeSink {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	DispatchClientConnection conn = new DispatchClientConnection();

	conn.setHost("127.0.0.1");
	conn.setPort(1510);

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
	com.cannontech.messaging.message.dispatch.RegistrationMessage reg = new com.cannontech.messaging.message.dispatch.RegistrationMessage();
	reg.setAppName("Van Gogh Test Client - Java" + (new java.util.Date()).getTime() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );

	conn.write( reg );

	//Do a loopback
	com.cannontech.clientutils.CTILogger.info("Attempting a loopback command");
	CommandMessage cmd = new CommandMessage();
	cmd.setOperation( CommandMessage.NO_OP );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	com.cannontech.clientutils.CTILogger.info("Received loopback:  " + response );
	
	//com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg();
	//dbChange.setDatabase(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ALL_DB);
	//dbChange.setDBType(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD);
	//conn.write( dbChange );

	// Register for point changes
	com.cannontech.clientutils.CTILogger.info("Registering for all point changes");
	com.cannontech.messaging.message.dispatch.PointRegistrationMessage pReg = new com.cannontech.messaging.message.dispatch.PointRegistrationMessage();
	pReg.setRegFlags( com.cannontech.messaging.message.dispatch.PointRegistrationMessage.REG_ALL_PTS_MASK);
	conn.write( pReg );
	
	//Wait for point change messages - single or multi
	com.cannontech.clientutils.CTILogger.info("Waiting for incoming messages....");
	for( ; ; )
	{
		Object in = null;
		try
		{
			in = conn.read();

			if( in == null )
			{
				Thread.sleep(200);
				continue;
			}
		}
		catch( InterruptedException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		
			com.cannontech.clientutils.CTILogger.info( in.toString() );		
	}
}
}
