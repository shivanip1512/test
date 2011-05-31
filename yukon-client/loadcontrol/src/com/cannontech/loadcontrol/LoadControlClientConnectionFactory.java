package com.cannontech.loadcontrol;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.StandaloneRoleDao;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.roles.yukon.SystemRole;

public class LoadControlClientConnectionFactory {
    private StandaloneRoleDao standaloneRoleDao;

    @Autowired
    public void setStandaloneRoleDao(StandaloneRoleDao standaloneRoleDao) {
		this.standaloneRoleDao = standaloneRoleDao;
	}
    
    
    public LoadControlClientConnection createConnection() {
    	// make connection
    	LoadControlClientConnection clientConnection = new LoadControlClientConnection();
    	
    	String lcHost = standaloneRoleDao.getGlobalPropertyValue(SystemRole.LOADCONTROL_MACHINE );
        String lcPortStr = standaloneRoleDao.getGlobalPropertyValue(SystemRole.LOADCONTROL_PORT);
        int lcPort = Integer.parseInt(lcPortStr);

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
