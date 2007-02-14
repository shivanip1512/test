package com.cannontech.notif.server;

import java.io.IOException;
import java.net.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.message.util.Message;
import com.cannontech.notif.outputs.OutputHandlerHelper;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.MBeanUtil;

/**
 * @author rneuharth
 *
 * The server used for accepting and creating notification messages.
 *
 */
public class NotificationServer implements Runnable, NotificationServerMBean
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
	
	// Total number of connections to the server made
	private long connsMade = 0;

    private NotifMsgHandler _msgHandler;

    private OutputHandlerHelper _outputHelper;
    
    private RoleDao roleDao;

	public NotificationServer() {
	}

	public int getBacklog() {
		return backlog;
	}

	public int getPort() {
		return port;
	}

	public void setBacklog(int i) {
		if( i <= 0 ) {
            i = 50;
        }
		backlog = i;
	}

	public void setPort(int i) {
		port = i;
	}

	public String getBindAddress() {
		String address = null;
		if( bindAddress != null ) {
            address = bindAddress.getHostAddress();
        }

		return address;
	}

	public void setBindAddress(String host) {
		try {
			if (host != null) {
                bindAddress = InetAddress.getByName(host);
            }
		} catch(UnknownHostException e) {
			String msg = "Invalid host address specified: " + host;
			CTILogger.error( msg, e );
		}
	}

	/**
     * Start the notification server.
     * If this fails with an exception, no threads will have been started.
	 * @throws IOException
	 */
	public void start() {
        try {
            setBindAddress(roleDao.getGlobalPropertyValue(SystemRole.NOTIFICATION_HOST) );
            setPort(Integer.parseInt(roleDao.getGlobalPropertyValue(SystemRole.NOTIFICATION_PORT)));        
            
            server = new ServerSocket(getPort(), getBacklog(), null);

            // start output handlers
            _outputHelper.startup();

            acceptThread = new Thread(this, "NotifListener");
            acceptThread.start();

            CTILogger.info("Started Notification server: " + server);
        } catch (Exception e) {
            try {
                if (server != null) {
                    server.close();
                }
            } catch (IOException e1) {}
            throw new RuntimeException(e);
        }

    }


	/**
     * Shutdown the notification server
     */
	public void stop() {
		try {
	
            if (server != null) {
                ServerSocket temp = server;
                server = null;
                temp.close();
            }
            
            // shutdown voice handler
            _outputHelper.shutdown();
		} catch (Exception e) {}
        
        CTILogger.info("Stopped Notification server: " + server);

	}

	public boolean isRunning() {
		return server != null;
	}

	/** 
	 * Listen threads entry point. Here we accept a client connection
	 */
	public void run() {
		for( ;; )
		{
			// Return if the server has been stopped
			if (server == null) {
                return;
            }


			// Accept a connection
			Socket socket = null;
			try {
				socket = server.accept();
				connsMade++;
			} catch (IOException e) {
				// If the server is not null meaning we were not stopped report the error
				if( server != null ) {
                    CTILogger.error("Failed to accept connection", e);
                }

				server = null;
				return;
			}

	
			//we have a connection, pass it off to another thread to process it
			try {
				//start handling the message here
				NotifServerConnection conn = new NotifServerConnection( socket, _msgHandler );

				conn.connectWithoutWait(); //passes control to another thread
			} catch (Exception ex) {
				CTILogger.error( "error handling socket connection", ex );
			}
		}
				 
	}
	
	public static void main( String[] argsv ) {
	    try {
	        System.setProperty("cti.app.name", "Notification-Server");
	        CTILogger.info("Starting notification server from main method");
            YukonSpringHook.setDefaultContext(YukonSpringHook.SERVICES_BEAN_FACTORY_KEY);
	        NotificationServer ns = (NotificationServer)YukonSpringHook.getBean("notificationServer");

	        MBeanUtil.tryRegisterMBean("type=notificationserver", ns);

	        ns.start();
	    }
	    catch( Throwable t )
	    {
	        CTILogger.error("There was an error starting up the Notification Server", t);
	    }
	}

    public void testInjectMessage(Message msg) {
        _msgHandler.testHandleMessage(msg);
    }

    public NotifMsgHandler getMsgHandler() {
        return _msgHandler;
    }

    public void setMsgHandler(NotifMsgHandler msgHandler) {
        _msgHandler = msgHandler;
    }

    public OutputHandlerHelper getOutputHelper() {
        return _outputHelper;
    }

    public void setOutputHelper(OutputHandlerHelper outputHelper) {
        _outputHelper = outputHelper;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
