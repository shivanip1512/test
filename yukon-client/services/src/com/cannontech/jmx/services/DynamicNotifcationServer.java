package com.cannontech.jmx.services;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.jmx.CTIBaseDynMBean;
import com.cannontech.notif.server.NotificationServer;

/**
 * @author rneuharth
 *
 * Wrapper for the NotifcationServer to allow plugability into a JMX server
 * 
 */
public class DynamicNotifcationServer extends CTIBaseDynMBean
{
	private NotificationServer notifServer = null;


	/**
	 * Main constructor
	 */
	public DynamicNotifcationServer()
	{
		super();
		
		notifServer = new NotificationServer();
		initialize();
	}

	
	/**
	 * Starts the Notification Service
	 */
	public void start()
	{
		try
		{
			notifServer.start();
		}
		catch( IOException ie )
		{
			CTILogger.error( "Could not start the Notification Server", ie );
		}

	}

	/**
	 * Stops the Notification Service
	 */
	public void stop()
	{
		notifServer.stop();
	}

	/**
	 * Returns the port number
	 */
	public int getPort()
	{
		return notifServer.getPort();
	}

	/**
	 * Sets the port number
	 */
	public void setPort( int port_ )
	{
		notifServer.setPort( port_ );
	}

	/**
	 * Returns the address
	 */
	public String getBindAddress()
	{
		return notifServer.getBindAddress();
	}

	/**
	 * Sets the address
	 */
	public void setBindAddress( String addr_ )
	{
		notifServer.setBindAddress( addr_ );
	}

	/**
	 * Gets the running state
	 */
	public boolean isRunning()
	{
		return notifServer.isRunning();
	}

	/**
	 * Returns the number of connections made to the service
	 */
	public long getConnectionsMade()
	{
		// The method 'getConnsMade' is private in the service, so here we
		// use reflection tricks
		try
		{
			Class cls = notifServer.getClass();
			Method method = cls.getDeclaredMethod("getConnsMade", new Class[0]);
			method.setAccessible(true);
			Long result = (Long)method.invoke(notifServer, new Object[0]);
			return result.longValue();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
			return 0;
		}

	}

	//
	// JMX Part
	//
	protected MBeanAttributeInfo[] createMBeanAttributeInfo()
	{
		return new MBeanAttributeInfo[]
		{
			new MBeanAttributeInfo("Port", "int", "The port number the Service is listening on", true, true, false),
			new MBeanAttributeInfo("BindAddress", String.class.getName(), "Bind adddress for the Service", true, true, false),
			new MBeanAttributeInfo("ConnectionsMade", "long", "The total number of connections made to the Service", true, false, false),
			new MBeanAttributeInfo("Running", "boolean", "The running status of the Service", true, false, true)
		};
	}

	protected MBeanOperationInfo[] createMBeanOperationInfo()
	{
		return new MBeanOperationInfo[]
		{
			new MBeanOperationInfo( 
				"start", "Start the Notification Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION),

			new MBeanOperationInfo( 
				"stop", "Stop the Notification Service", 
				new MBeanParameterInfo[0], "void", MBeanOperationInfo.ACTION)
		};
	}

}
