package com.cannontech.loadcontrol;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.messaging.util.ConnectionFactoryService;

public class LoadControlClientConnectionFactory {
    
    @Autowired private ConnectionFactoryService connectionFactorySvc;
    
    public LoadControlClientConnection createConnection() {
    	// make connection
    	LoadControlClientConnection clientConnection = new LoadControlClientConnection();
        clientConnection.setConnectionFactory(connectionFactorySvc.findConnectionFactory("LC"));
    	
    	CTILogger.info("Will attempt to connect to loadcontrol @" + clientConnection);
    	    		
    	clientConnection.connectWithoutWait();
    	
    	// init
    	clientConnection.addMessageListener( clientConnection );
        LMCommand cmd = new LMCommand();
        cmd.setCommand( LMCommand.RETRIEVE_ALL_CONTROL_AREAS );
        clientConnection.setRegistrationMsg(cmd);

        return clientConnection;
    }
}
