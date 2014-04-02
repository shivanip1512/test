package com.cannontech.message.dispatch.message;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * A class to help send System Log events in a straight forward fasion.
 * Instantiate the class with an existing ClientConnection (to dispatch)
 * or call the constructor with no arguments to create a default connection.
 */
public class SystemLogHelper {

    private final IServerConnection _dispatchConnection;
    private final int _signalPointId;

    /**
     * Create a SystemLogHelper with an existing dispatch connection.
     * @param dispatchConnection
     */
    public SystemLogHelper(int signalPointId, IServerConnection dispatchConnection) {
        _signalPointId = signalPointId;
        _dispatchConnection = dispatchConnection;
    }
    
    /**
     * Create a SystemLogHelper with a default dispatch connection.
     */
    public SystemLogHelper(int signalPointType) {
        this(signalPointType, ConnPool.getInstance().getDefDispatchConn());
    }

    /**
     * Send a log message to dispatch.  The clientsession username will be used.
     * @param action
     * @param description
     */
    public void log(String action, String description) {
        log(_signalPointId, action, description, CtiUtilities.getUserName());
    }

    /**
     * Send a log message to dispatch.
     * @param action
     * @param description
     */
    public void log(int signalPointID, String action, String description, String username) {
    	log(signalPointID, action, description, username, SystemLog.TYPE_GENERAL);
    } 
    
    /**
     * Send a log message to dispatch.
     * @param action
     * @param description
     */
    public void log(String action, String description, LiteYukonUser user) {
        log(_signalPointId, action, description, user.getUsername(), SystemLog.TYPE_GENERAL);
    }   
    
    /**
     * Send a log message to dispatch.
     * @param action
     * @param description
     */
    public void log(int signalPointID, String action, String description, String username, int logType) {
        Signal sig = new Signal();
        sig.setPointID(signalPointID);
        sig.setDescription(StringUtils.abbreviate(description, 120));
        sig.setAction(action);
        sig.setLogType(logType);
        if( username != null)
            sig.setUserName(username);
        sig.setCategoryID(Signal.EVENT_SIGNAL);

        _dispatchConnection.queue(sig);
    }
}
