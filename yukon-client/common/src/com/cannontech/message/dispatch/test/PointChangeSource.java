package com.cannontech.message.dispatch.test;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.message.dispatch.ClientConnection;

public class PointChangeSource {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	if( args.length < 5 )
	{
		com.cannontech.clientutils.CTILogger.info("Usage:  PointChangeSource vangoghmachine port numberofchanges delay { pointID }");
		com.cannontech.clientutils.CTILogger.info("specify numberofchanges = -1 to keep sending changes forever");
		com.cannontech.clientutils.CTILogger.info("note that port 1510 has been the default");
		System.exit(0);
	}

	String vanGogh = args[0];
	int port = (Integer.decode(args[1])).intValue();
	int numChanges = (Integer.decode(args[2])).intValue();
	int delay = (Integer.decode(args[3])).intValue();

	int[] id = new int[ args.length - 4 ];

	for( int i = 0; i < id.length; i++ )
	{
		id[i] = (Integer.decode( args[4+i] )).intValue();
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
	reg.setAppName("Point Change Source - Java" + (new java.util.Date()).getTime() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );

	conn.write( reg );

	//Do a loopback
	com.cannontech.clientutils.CTILogger.info("Attempting a loopback command");
	com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
	cmd.setOperation( com.cannontech.message.dispatch.message.Command.NO_OP );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	com.cannontech.clientutils.CTILogger.info("Received loopback:  " + response );

	//Send changes
	int numSent = 0;

	while( forever || numSent < numChanges )
	{

		com.cannontech.message.dispatch.message.Multi outMsg = new com.cannontech.message.dispatch.message.Multi();
		
		for( int j = 0; j < id.length; j++ )
		{				
			com.cannontech.message.dispatch.message.PointData pData = new com.cannontech.message.dispatch.message.PointData();	
			com.cannontech.message.dispatch.message.Signal signal = new com.cannontech.message.dispatch.message.Signal();

			//pData.setSig(signal);
			pData.setType(1); //what is this ?
			pData.setLimit(150);
			//pData.setOffset( 10);
			pData.setStr("Test Point Change");
			pData.setTags(0);
			pData.setValue( Math.random() * 100.0 );
			pData.setTime( new java.util.Date() );
			
			signal.setAlarmStateID(6);
			//signal.setFlag(0);
			signal.setDescription("Test Signal");
			
			pData.setId( id[j] );
			signal.setId( id[j] );

			outMsg.getVector().addElement( pData ); 
		}
			
		conn.write( outMsg );
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
