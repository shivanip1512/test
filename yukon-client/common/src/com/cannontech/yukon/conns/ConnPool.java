
package com.cannontech.yukon.conns;

import java.util.Hashtable;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.INotifConnection;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;

/**
 * @author rneuharth
 *
 * Pool for connections to all yukon servers
 *
 */
public class ConnPool
{	
	private static ConnPool _poolInstance = null;
	

    //default keys that are used in the pool. New connections
    // will be made based on the type found in the given string.
    // This process is not case sensitive.
    //    
    //  example: TDC_PORTER creates a new Porter type connection
    //           with the given String as its name
    // 
	public static final String PORTER_CONN = "PORTER";
	public static final String DISPATCH_CONN = "DISPATCH";
	public static final String MACS_CONN = "MACS";
	public static final String CAPCONTROL_CONN = "CAPCONTROL";
	public static final String NOTIFCATION_CONN = "NOTIFCATION";
	

	// Map<String, IServerConnection>
	private Hashtable _allConns = null;


	/**
	 * Returns the singleton instance of this class
	 *
	 */
	public static synchronized ConnPool getInstance()
	{
		if( _poolInstance == null ) 
		{
			_poolInstance = new ConnPool();
		}

		return _poolInstance;
	}

	private Hashtable getAllConns()
	{
		if( _allConns == null )
			_allConns = new Hashtable(16);
		
		return _allConns;
	}
    

    /**
     * Gets a connection from the pool. If the connName is not found, then a new
     * connection is made.
     * 
     */
    public synchronized IServerConnection getConn( String connName )
	{
		IServerConnection conn =
			(IServerConnection)getAllConns().get( connName );
			
		//we do not have a conn by that name, create one
		if( conn == null && connName != null )
		{
            if( connName.toUpperCase().indexOf(PORTER_CONN) >= 0 )
            {
                conn = createPorterConn();
            }
            else if( connName.toUpperCase().indexOf(DISPATCH_CONN) >= 0 )
            {
                conn = createDispatchConn();
            }
            else if( connName.toUpperCase().indexOf(MACS_CONN) >= 0 )
            {
                conn = createMacsConn();
            }
            else if( connName.toUpperCase().indexOf(CAPCONTROL_CONN) >= 0 )
            {
                conn = createCapControlConn();
            }
            else
            {
                throw new IllegalArgumentException(
                        "Unable to create connection with the given connection string: " + connName );
            }

            //add wichever conn we made into our Pool
            getAllConns().put( connName, conn );            
        }

		return conn;
	}


    /**
     * Creates a new Porter connection.
     * 
     */
	private IServerConnection createPorterConn()
	{		
		com.cannontech.message.porter.ClientConnection porterCC = 
				new com.cannontech.message.porter.ClientConnection();

		porterCC.setQueueMessages(false);	//don't keep messages, toss once read.
		porterCC.setAutoReconnect(true);

		return porterCC;
	}

	/**
	 * Creates a new Dispatch connection.
	 * 
	 */
	private IServerConnection createDispatchConn()
	{		
		ClientConnection connToDispatch = new ClientConnection();

		Registration reg = new Registration();
		reg.setAppName("Generic_Dispatch_Conn");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(300); // 5 minutes should be OK
	
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);
		
		return connToDispatch;
	}

	/**
	 * Returns a ClientConnection to dispatch. The connection is returned in an
	 * connected state. Notice that no PointRegistration is done here.
	 * 
	 */
	public IServerConnection getDefDispatchConn() {

		//check our master Map of existing connections
		ClientConnection connToDispatch =
			(ClientConnection)getAllConns().get(DISPATCH_CONN);

		if( connToDispatch == null ) {
			String defaultHost = "127.0.0.1";
			int defaultPort = 1510;
	
			try {
				defaultHost = RoleFuncs.getGlobalPropertyValue(SystemRole.DISPATCH_MACHINE);
	
				defaultPort = Integer.parseInt(RoleFuncs.getGlobalPropertyValue(SystemRole.DISPATCH_PORT));
			} catch (Exception e) {
				CTILogger.warn("Could not get host and port for dispatch connection from Role Properties, using defaults", e);
			}
	
			connToDispatch = (ClientConnection)createDispatchConn();
			Registration reg = new Registration();
			reg.setAppName("Default_Conn");
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay(300); // 5 minutes should be OK
	
			connToDispatch.setHost(defaultHost);
			connToDispatch.setPort(defaultPort);
			connToDispatch.setAutoReconnect(true);
			connToDispatch.setRegistrationMsg(reg);
	
			try 
			{
				CTILogger.info("Attempting Dispatch connection to " + connToDispatch.getHost() + ":" + connToDispatch.getPort());
				connToDispatch.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				CTILogger.error( e.getMessage(), e );
			}
	                
			getAllConns().put( DISPATCH_CONN, connToDispatch); 
		}

		return connToDispatch;
	}

    /**
     * Gets the default Porter connection that is available to all users.
     * If not found, we create a default connection that tries connecting
     * right away.
     * 
     */
    public IServerConnection getDefPorterConn()
    {
        //check our master Map of existing connections
        com.cannontech.message.porter.ClientConnection porterCC =
            (com.cannontech.message.porter.ClientConnection)getAllConns().get(PORTER_CONN);
        
        if( porterCC == null )
        {
            String host = "127.0.0.1";
            int port = 1510;
            
            try
            {
                host = RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_MACHINE );
    
                port = Integer.parseInt( 
                    RoleFuncs.getGlobalPropertyValue( SystemRole.PORTER_PORT ) ); 
            }
            catch( Exception e)
            {
                CTILogger.error( e.getMessage(), e );
            }
    
            porterCC = (com.cannontech.message.porter.ClientConnection)createPorterConn();
    
            porterCC.setHost(host);
            porterCC.setPort(port);
            
            try 
            {
                CTILogger.info("Attempting Porter connection to " + porterCC.getHost() + ":" + porterCC.getPort());
                porterCC.connectWithoutWait();
            }
            catch( Exception e ) 
            {
                CTILogger.error( e.getMessage(), e );
            }
                
            getAllConns().put( PORTER_CONN, porterCC );            
        }
                    
        return porterCC;        
        
    }

    /**
     * Gets the default Macs connection that is available to all users.
     *
     */
    public IServerConnection getDefMacsConn()
    {
        //check our master Map of existing connections
    	IServerConnection macsConn =
            (IServerConnection)getAllConns().get(MACS_CONN);

        if( macsConn == null )
        {		
        	macsConn = createMacsConn();

			getAllConns().put( MACS_CONN, macsConn );
        }
        
        return macsConn;    	
    }

    /**
     * Creates a new MACS connection.
     * 
     */
    private IMACSConnection createMacsConn()
    {       
        ServerMACSConnection macsConn = new ServerMACSConnection();

        return macsConn;
    }

    /**
     * Gets the default CapControl connection that is available to all users.
     *
     */
    public IServerConnection getDefCapControlConn()
    {
        //check our master Map of existing connections
        CBCClientConnection cbcConn =
            (CBCClientConnection)getAllConns().get(CAPCONTROL_CONN);

        if( cbcConn == null )
        {		
        	cbcConn = (CBCClientConnection)createCapControlConn();

			try
			{
				//start the conn!!!
				cbcConn.connect( 15000 );
				
				cbcConn.executeCommand( 0, CBCCommand.REQUEST_ALL_SUBS );
			}
			catch( java.io.IOException ex )
			{
				CTILogger.error( ex );
			}
			
			getAllConns().put( CAPCONTROL_CONN, cbcConn );
        }
        
        return cbcConn;
    }

	/**
	 * Gets the default NotifcationClient connection that is available to all users.
	 *
	 */
	public INotifConnection getDefNotificationConn()
	{
		//check our master Map of existing connections
		NotifClientConnection notifConn =
			(NotifClientConnection)getAllConns().get(NOTIFCATION_CONN);

		if( notifConn == null )
		{		
			notifConn = (NotifClientConnection)createNotificationConn();

			notifConn.connectWithoutWait();
			
			getAllConns().put( NOTIFCATION_CONN, notifConn );
		}
        
		return notifConn;
	}

    /**
     * Creates a new CapControl connection.
     * 
     */
    private IServerConnection createCapControlConn()
    {       
    	CBCClientConnection cbcConn = new CBCClientConnection();

        return cbcConn;
    }
    
	/**
	 * Creates a new NotifServer connection.
	 * 
	 */
	private IServerConnection createNotificationConn()
	{       
		NotifClientConnection notifConn = new NotifClientConnection();

		return notifConn;
	}

}