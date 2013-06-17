package com.cannontech.loadcontrol;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class LoadControlClientConnectionFactory {
    
    @Autowired private GlobalSettingDao globalSettingDao;
    
    public LoadControlClientConnection createConnection() {
    	// make connection
    	LoadControlClientConnection clientConnection = new LoadControlClientConnection();
    	
    	String lcHost = globalSettingDao.getString(GlobalSettingType.LOADCONTROL_MACHINE );
        int lcPort = globalSettingDao.getInteger(GlobalSettingType.LOADCONTROL_PORT);

    	CTILogger.info("Will attempt to connect to loadcontrol @" + lcHost + ":" + lcPort);
    	if ( lcHost != null ) {
			clientConnection.setHost(lcHost);
		}
    	if ( lcPort != -1 ) {
			clientConnection.setPort(lcPort);
		}
    		
    	clientConnection.connectWithoutWait();
    	
    	// init
    	clientConnection.addMessageListener( clientConnection );
        LMCommand cmd = new LMCommand();
        cmd.setCommand( LMCommand.RETRIEVE_ALL_CONTROL_AREAS );
        clientConnection.setRegistrationMsg(cmd);

        return clientConnection;
    }
}
