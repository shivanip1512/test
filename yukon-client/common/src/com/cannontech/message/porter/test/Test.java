package com.cannontech.message.porter.test;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 2:22:43 PM)
 * @author: 
 */
public class Test {
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
		com.cannontech.message.porter.ClientConnection conn = new com.cannontech.message.porter.ClientConnection();
		conn.setHost("10.100.9.230");
		conn.setPort(1540);
		conn.connect();
		com.cannontech.message.porter.message.Request req = new com.cannontech.message.porter.message.Request(deviceID, command, 1);
		conn.write(req);
		Object o = conn.read();
		if (o instanceof com.cannontech.message.porter.message.Return)
		{
			com.cannontech.message.porter.message.Return r = (com.cannontech.message.porter.message.Return) o;
			com.cannontech.clientutils.CTILogger.info(r);
		}
	}
	catch (java.io.IOException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}
}
