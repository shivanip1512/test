package com.cannontech.stars.util;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.message.porter.ClientConnection;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServerUtils {
    
    private static PILConnectionServlet connContainer = null;

    // Increment this for every message
    private static long userMessageIDCounter = 1;

    public static void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        CTILogger.debug( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
    
    public static void setConnectionContainer(PILConnectionServlet servlet) {
    	connContainer = servlet;
    }
    
    public static ClientConnection getClientConnection() {
    	if (connContainer == null) return null;
    	
    	return connContainer.getConnection();
    }
}
