package com.cannontech.loadcontrol;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

public class LoadControlClientConnectionFactory {
    
    @Autowired private YukonSettingsDao yukonSettingDao;
    
    public LoadControlClientConnection createConnection() {
    	// make connection
    	LoadControlClientConnection clientConnection = new LoadControlClientConnection();
    	
    	String lcHost = yukonSettingDao.getSettingStringValue(YukonSetting.LOADCONTROL_MACHINE );
        int lcPort = yukonSettingDao.getSettingIntegerValue(YukonSetting.LOADCONTROL_PORT);

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
