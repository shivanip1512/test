package com.cannontech.message.dispatch.test;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.ClientConnectionFactory;

public class DBChangeSource {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	if( args.length != 2 )
	{
		CTILogger.info("Usage:  DBChangeSource numberofchanges delay");
		CTILogger.info("specify numberofchanges = -1 to keep sending changes forever");
		System.exit(0);
	}

	int numChanges = (Integer.decode(args[0])).intValue();
	int delay = (Integer.decode(args[1])).intValue();

	boolean forever = false;

	if( numChanges == -1 ) {
        forever = true;
    }

	DispatchClientConnection conn = ClientConnectionFactory.getInstance().createDispatchConn();

	conn.connect();

	//First do a registration
	CTILogger.info("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Datbase Change Source - Java" + (new java.util.Date()).getTime() );
	reg.setAppIsUnique(0);
	reg.setAppExpirationDelay( 1000000 );

	conn.write( reg );

	//Send changes
	int numSent = 0;

	while( forever || numSent < numChanges )
	{
		//com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg();
		//dbChange.setDatabase(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ALL_DB);
		//dbChange.setDBType(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD);
		//conn.write( dbChange );
		numSent++;
		CTILogger.info("Sent change #" + numSent);
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
}
