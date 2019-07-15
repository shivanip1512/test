package com.cannontech.tools.msg;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
/**
 * Insert the type's description here.
 * Creation date: (6/13/00 10:50:34 AM)
 * @author:
 */
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.util.ClientConnectionFactory;
import com.cannontech.message.util.Command;

public class DBChangeGenerator {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	if( args.length < 3 )
	{
		com.cannontech.clientutils.CTILogger.info("Usage:  DBChangeGenerator numberofchanges delay pointcount { pointID }");
		com.cannontech.clientutils.CTILogger.info("specify numberofchanges = -1 to keep sending changes forever");
		System.exit(0);
	}

	int numChanges = (Integer.decode(args[0])).intValue();
	int delay = (Integer.decode(args[1])).intValue();
	int pointCount = (Integer.decode(args[2])).intValue();

	int[] id = new int[ pointCount ];

	for( int i = 0; i < id.length; i++ ) {
        id[i] = (Integer.decode( args[3+i] )).intValue();
    }

	boolean forever = false;

	if( numChanges == -1 ) {
        forever = true;
    }

	DispatchClientConnection conn = ClientConnectionFactory.getInstance().createDispatchConn();


	conn.connect();

	//First do a registration
	com.cannontech.clientutils.CTILogger.info("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName( CtiUtilities.getAppRegistration() );
	reg.setAppIsUnique(0);
	reg.setAppExpirationDelay( 300 );

	conn.write( reg );

	//Do a loopback
	com.cannontech.clientutils.CTILogger.info("Attempting a loopback command");
	Command cmd = new Command();
	cmd.setOperation( Command.LOOP_CLIENT );
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
			DBChangeMsg msg = new DBChangeMsg(
					id[j],
					DBChangeMsg.CHANGE_PAO_DB,
					PaoCategory.DEVICE.getDbString(),
					PaoType.CAPBANK.getDbString(),
					DbChangeType.UPDATE );

			msg.setTimeStamp( new java.util.Date() );

			conn.write( msg );  // write the signal
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
