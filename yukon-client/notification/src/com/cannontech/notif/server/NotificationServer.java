package com.cannontech.notif.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.GenericDBCacheHandler;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.roles.yukon.SystemRole;

/**
 * @author rneuharth
 *
 * The server used for accepting and creating notification messages.
 *
 */
public class NotificationServer implements Runnable
{
	// The port the web server listens on
	private int port = 1515;

	// The interface to bind to
	private InetAddress bindAddress;

	// The serverSocket listen queue depth
	private int backlog = 50;

	// The servers listening socket
	private ServerSocket server = null;

	// The thread accept any incoming connections
	private Thread acceptThread = null;
	

	//since this guy uses the cache, he better care about DBUpdates
	private GenericDBCacheHandler dbCacheHandler = null;

	// Total number of connections to the server made
	private long connsMade = 0;


	/**
	 * 
	 */
	public NotificationServer()
	{
		super();
		setBindAddress(
                RoleFuncs.getGlobalPropertyValue(SystemRole.NOTIFICATION_HOST) );
        
        setPort( Integer.parseInt(
                RoleFuncs.getGlobalPropertyValue(SystemRole.NOTIFICATION_PORT) ) );        
	}

	/**
	 * @return
	 */
	public int getBacklog()
	{
		return backlog;
	}

	/**
	 * @return
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @param i
	 */
	public void setBacklog(int i)
	{
		if( i <= 0 )
			i = 50;
		backlog = i;
	}

	/**
	 * @param i
	 */
	public void setPort(int i)
	{
		port = i;
	}

	public String getBindAddress()
	{
		String address = null;
		if( bindAddress != null )
			address = bindAddress.getHostAddress();

		return address;
	}

	public void setBindAddress(String host)
	{
		try
		{
			if (host != null)
				bindAddress = InetAddress.getByName(host);
		}
		catch(UnknownHostException e)
		{
			String msg = "Invalid host address specified: " + host;
			CTILogger.error( msg, e );
		}
	}

	/**
	 * Start the notification server on port and begin listening for requests.
	 */
	public void start() throws IOException
	{
		try
		{
			server = new ServerSocket( 
					getPort(), 
					getBacklog(), 
					null ); //bindAddress );

			CTILogger.info("Started Notification server: " + server);


			dbCacheHandler = new GenericDBCacheHandler("NotificationServer");
			DefaultDatabaseCache.getInstance().addDBChangeListener( dbCacheHandler );

			acceptThread = new Thread( this, "NotifListener" );
			acceptThread.start();
		}
		catch (IOException e)
		{
			throw e;
		}
	}


	/** 
	 * Close the notification server listening socket
	 */
	public void stop()
	{
		try
		{
			dbCacheHandler.getClientConnection().disconnect();
			DefaultDatabaseCache.getInstance().removeDBChangeListener( dbCacheHandler );
			
			ServerSocket srv = server;
			server = null;
			srv.close();
		}
		catch (Exception e)
		{}
	}

	public boolean isRunning()
	{
		return server != null;
	}

	private long getConnsMade()
	{
		return connsMade;
	}

	/** 
	 * Listen threads entry point. Here we accept a client connection
	 */
	public void run()
	{
		for( ;; )
		{
			// Return if the server has been stopped
			if (server == null)
				return;


			// Accept a connection
			Socket socket = null;
			try 
			{
				socket = server.accept();
				connsMade++;
			}
			catch (IOException e) 
			{
				// If the server is not null meaning we were not stopped report the error
				if( server != null )
					CTILogger.error("Failed to accept connection", e);

				server = null;
				return;
			}

	
			//we have a connection, pass it off to another thread to process it
			try 
			{
				//start handling the message here
				NotifServerConnection conn = new NotifServerConnection( socket );

				conn.connectWithoutWait(); //passes control to another thread
			}
			catch (Exception ex)
			{
				CTILogger.error( "error handling socket connection", ex );
			}
		}
				 
	}
	
	
	
	public static void main( String[] argsv )
	{
		System.setProperty("cti.app.name", "Notification-Server");
		NotificationServer ns = new NotificationServer();
		
		try 
		{
			ns.start();
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}

	}

}
