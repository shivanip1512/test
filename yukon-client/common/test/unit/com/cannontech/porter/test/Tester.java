package com.cannontech.porter.test;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 2:22:43 PM)
 * @author: 
 */
public class Tester {
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 2:23:00 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	try
	{
		int deviceID = 1;
		String command = "GetStatus";
		com.cannontech.porter.PorterClientConnection conn = new com.cannontech.porter.PorterClientConnection();
		conn.setHost("10.100.9.230");
		conn.setPort(1540);
		conn.connect();
		com.cannontech.messaging.message.porter.RequestMessage req = new com.cannontech.messaging.message.porter.RequestMessage(deviceID, command, 1);
		conn.write(req);
		Object o = conn.read();
		if (o instanceof com.cannontech.messaging.message.porter.ReturnMessage)
		{
			com.cannontech.messaging.message.porter.ReturnMessage r = (com.cannontech.messaging.message.porter.ReturnMessage) o;
			com.cannontech.clientutils.CTILogger.info(r);
		}
	}
	catch (java.io.IOException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}
}
