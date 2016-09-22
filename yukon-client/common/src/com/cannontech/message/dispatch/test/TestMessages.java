package com.cannontech.message.dispatch.test;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.ClientConnectionFactory;
import com.cannontech.message.util.Command;

public class TestMessages {
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	DispatchClientConnection conn = ClientConnectionFactory.getInstance().createDispatchConn();
	com.cannontech.message.dispatch.message.Multi multiReg = new com.cannontech.message.dispatch.message.Multi();

	//First do a registration
	com.cannontech.clientutils.CTILogger.info("Registering client with vangogh");
	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Van Gogh Test Client - Java");
	reg.setAppIsUnique(0);
	reg.setAppExpirationDelay( 1000000 );
	multiReg.getVector().addElement(reg);

	//Register for DBChangeMsg
	com.cannontech.clientutils.CTILogger.info("Registering for database changes");
	//com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg();
	//dbChange.setDatabase(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_ALL_DB);
	//dbChange.setDBType(com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_ADD);
	//multiReg.getVector().addElement(dbChange);

	// Register for point changes
	com.cannontech.clientutils.CTILogger.info("Registering for all point changes");
	com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
	pReg.setRegFlags( com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_POINTS | com.cannontech.message.dispatch.message.PointRegistration.REG_ALARMS | com.cannontech.message.dispatch.message.PointRegistration.REG_EVENTS );
	multiReg.getVector().addElement(pReg);

	conn.setRegistrationMsg(multiReg);

	conn.connect();

	//Do a loopback
	com.cannontech.clientutils.CTILogger.info("Attempting a loopback command");
	Command cmd = new Command();
	cmd.setOperation( Command.NO_OP );
	conn.write( cmd );

	//Expect the message back
	Object response = conn.read();
	com.cannontech.clientutils.CTILogger.info("Received loopback:  " + response );


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
