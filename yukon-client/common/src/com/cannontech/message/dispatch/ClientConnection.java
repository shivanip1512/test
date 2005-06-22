package com.cannontech.message.dispatch;

/**
 * This type was created in VisualAge.
 */
import java.io.IOException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.Message;
import com.cannontech.roles.yukon.SystemRole;
import com.roguewave.vsj.CollectableStreamer;

public class ClientConnection extends com.cannontech.message.util.ClientConnection 
{
/**
 * ClientConnection constructor comment.
 */
public ClientConnection() {
	super();
}
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
public ClientConnection(String host, int port) {
	super(host, port);
}

protected void fireMessageEvent(Message msg) 
{
	if( msg instanceof Command
		 && ((Command)msg).getOperation() == Command.ARE_YOU_THERE )
	{
		// Only instances of com.cannontech.message.dispatch.message.Command should
		// get here and it should have a ARE_YOU_THERE operation
		// echo it back so vangogh doesn't time out on us

		write( msg );
	}

	super.fireMessageEvent( msg );
}

/**
 * This method was created in VisualAge.
 */
protected void registerMappings(CollectableStreamer polystreamer) {
	super.registerMappings(polystreamer);

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();
	
	for( int i = 0; i < mappings.length; i++ )
	{
		polystreamer.register( mappings[i] );
	}

//	polystreamer.register()
}
  public static ClientConnection createDefaultConnection() throws IOException {
        String defaultHost = "127.0.0.1";
        int defaultPort = 1510;

        try {
            defaultHost = RoleFuncs.getGlobalPropertyValue(SystemRole.DISPATCH_MACHINE);

            defaultPort = Integer.parseInt(RoleFuncs.getGlobalPropertyValue(SystemRole.DISPATCH_PORT));
        } catch (Exception e) {
            CTILogger.warn("Could not get host and port for dispatch connection from Role Properties, using defaults", e);
        }

        ClientConnection connToDispatch = new ClientConnection();
        Registration reg = new Registration();
        reg.setAppName(CtiUtilities.getAppRegistration());
        reg.setAppIsUnique(0);
        reg.setAppKnownPort(0);
        reg.setAppExpirationDelay(300); // 5 minutes should be OK

        connToDispatch.setHost(defaultHost);
        connToDispatch.setPort(defaultPort);
        connToDispatch.setAutoReconnect(true);
        connToDispatch.setRegistrationMsg(reg);

        connToDispatch.connectWithoutWait();
        return connToDispatch;
    }

}
