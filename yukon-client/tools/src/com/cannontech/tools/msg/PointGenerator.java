package com.cannontech.tools.msg;

/**
 * Insert the type's description here.
 * Creation date: (6/13/00 10:32:57 AM)
 * @author: 
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

public class PointGenerator implements MessageListener 
{
	private int cnt = 0;
	
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	if( args.length < 5 )
	{
		CTILogger.info("Usage:  PointChangeSource vangoghmachine port numberofchanges delay pointcount { pointID } { pointType }");
		CTILogger.info("specify numberofchanges = -1 to keep sending changes forever");
		CTILogger.info("note that port 1510 has been the default");
		CTILogger.info("PointTypes : 0=Status  1=Analog  2=PulsAccum  3=DmdAccum  4=Calculated");		
		System.exit(0);
	}
	
	
	String vanGogh = args[0];
	int port = (Integer.decode(args[1])).intValue();
	int numChanges = (Integer.decode(args[2])).intValue();
	int delay = (Integer.decode(args[3])).intValue();
	int pointCount = (Integer.decode(args[4])).intValue();
	
	int[] id = new int[ pointCount ];
	int[] type = new int[ pointCount ];

	for( int i = 0; i < id.length; i++ )
	{
		id[i] = (Integer.decode( args[5+i] )).intValue();
	}

	for( int i = 0; i < type.length; i++ )
	{
		type[i] = (Integer.decode( args[5+pointCount+i] )).intValue();
	}
	
	boolean forever = false;

	if( numChanges == -1 )
	 	forever = true;
	
	ClientConnection conn = new ClientConnection();
	PointRegistration pr = new PointRegistration();
	pr.setRegFlags( PointRegistration.REG_ALL_PTS_MASK );
	conn.setRegistrationMsg( pr );
	conn.addMessageListener( new PointGenerator() );
	
	conn.setQueueMessages(false);

	conn.setHost(vanGogh);
	conn.setPort(port);

	try
	{
		conn.connect();
	}
	catch( java.io.IOException e )
	{
		CTILogger.error( e.getMessage(), e );
		System.exit(0);
	}

	//First do a registration
	CTILogger.info("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Point Change Source - Java" + (new java.util.Date()).getTime() );
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 300 );

	conn.write( reg );

	//Do a loopback
/*	CTILogger.info("Attempting a loopback command");
	com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
	cmd.setOperation( com.cannontech.message.dispatch.message.Command.NO_OP );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	CTILogger.info("Received loopback:  " + response );*/

	//Send changes
	int numSent = 0;

	while( forever || numSent < numChanges )
	{

		com.cannontech.message.dispatch.message.Multi outMsg = new com.cannontech.message.dispatch.message.Multi();
		
		for( int j = 0; j < id.length; j++ )
		{				
			com.cannontech.message.dispatch.message.PointData pData = new com.cannontech.message.dispatch.message.PointData();	

			pData.setType(type[j]);
			
			pData.setLimit(150);
			pData.setAttributes(0);
			pData.setStr("Test Point Change");
			pData.setTags(0);

			// We are unable to set a PointData Attributes
			//pData.setAttributes( com.cannontech.message.dispatch.message.PointData.ATTRIB_CONTROL_AVAILABLE );

			if( type[j] == 0 )
				pData.setValue( Math.round(Math.random() * 6.0) );  // 0-6 values for STATUS points
			else
				pData.setValue( Math.random() * 100.0 );
				
			pData.setTime( new java.util.Date() );
			
			
			pData.setId( id[j] );

			outMsg.getVector().addElement( pData ); 
		}
			
		conn.write( outMsg );
		numSent++;
		
		CTILogger.info((new java.util.Date()).toString() + " - Sent change #" + numSent);
		try
		{
			Thread.sleep(delay);
		}
		catch( InterruptedException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
	}

	System.exit(0);
}


public void messageReceived(MessageEvent e)
{
	cnt++;
	if( (cnt % 20) == 0 )
		CTILogger.info( "   MEM1= " + Runtime.getRuntime().freeMemory() + " / " 
			+ Runtime.getRuntime().totalMemory() );

}

}
