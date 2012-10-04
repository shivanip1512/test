
package com.cannontech.yukon.conns;

import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.INotifConnection;
import com.cannontech.yukon.IServerConnection;

/**
 * @author rneuharth
 *
 * Pool for connections to all yukon servers
 *
 */
public class ConnPool
{	

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
	@Autowired private GlobalSettingsDao globalSettingsDao;

	// Map<String, IServerConnection>
	private Hashtable<String, IServerConnection> _allConns = null;

	/**
	 * Returns the singleton instance of this class
	 *
	 */
	public static synchronized ConnPool getInstance()
	{
		return (ConnPool) YukonSpringHook.getBean("connectionPool");
	}
  
	private Hashtable<String, IServerConnection> getAllConns()
	{
		if( _allConns == null )
			_allConns = new Hashtable<String, IServerConnection>(16);
		
		return _allConns;
	}
    
    public void shutdown() {
        for (IServerConnection conn : _allConns.values()) {
            conn.disconnect();
        }
    }
    

    /**
     * Creates a new Porter connection.
     * 
     */
	private IServerConnection createPorterConn()
	{		
		com.cannontech.message.porter.ClientConnection porterCC = 
				new com.cannontech.message.porter.ClientConnection();

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
         /*
         * App name will be value of cti.app.name environment variable
         */
        reg.setAppName(CtiUtilities.getApplicationName());
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(300); // 5 minutes should be OK
	        
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

	        defaultHost = globalSettingsDao.getString(GlobalSetting.DISPATCH_MACHINE);
	        int port = globalSettingsDao.getInteger(GlobalSetting.DISPATCH_PORT);

	        connToDispatch = (ClientConnection)createDispatchConn();
	        connToDispatch.setHost(defaultHost);
	        connToDispatch.setPort(port);
	        
	        CTILogger.info("Attempting Dispatch connection to " + connToDispatch.getHost() + ":" + connToDispatch.getPort());
	        connToDispatch.connectWithoutWait();

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
            
            host = globalSettingsDao.getString(GlobalSetting.PORTER_MACHINE);
            int port = globalSettingsDao.getInteger(GlobalSetting.PORTER_PORT);
    
            porterCC = (com.cannontech.message.porter.ClientConnection)createPorterConn();
    
            porterCC.setHost(host);
            porterCC.setPort(port);
            
            CTILogger.info("Attempting Porter connection to " + porterCC.getHost() + ":" + porterCC.getPort());
            porterCC.connectWithoutWait();

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
//      check our master Map of existing connections
        ServerMACSConnection macsConn = (ServerMACSConnection)getAllConns().get(MACS_CONN);
        
        if( macsConn == null ) {
            
            String host = "127.0.0.1";

            host = globalSettingsDao.getString(GlobalSetting.MACS_MACHINE);
            int port = globalSettingsDao.getInteger(GlobalSetting.MACS_PORT);

            macsConn = (ServerMACSConnection)createMacsConn();
    
            macsConn.setHost(host);
            macsConn.setPort(port);
            
            macsConn.setRegistrationMsg(macsConn.getRetrieveAllSchedulesMsg());

            CTILogger.info("Attempting MACS connection to " + macsConn.getHost() + ":" + macsConn.getPort());
            macsConn.connectWithoutWait();

            getAllConns().put( MACS_CONN, macsConn );            
        }
                    
        return macsConn;
    }

    /**
     * Creates a new MACS connection.
     * 
     */
    private IMACSConnection createMacsConn() {       
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
        CapControlClientConnection cbcConn =
            (CapControlClientConnection)getAllConns().get(CAPCONTROL_CONN);

        if( cbcConn == null )
        {		
        	cbcConn = (CapControlClientConnection)createCapControlConn();
            cbcConn.setHost(globalSettingsDao.getString(GlobalSetting.CAP_CONTROL_MACHINE));
            int port = globalSettingsDao.getInteger(GlobalSetting.CAP_CONTROL_PORT);
            cbcConn.setPort(port);

            cbcConn.connectWithoutWait();
			
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
    	CapControlClientConnection cbcConn = new CapControlClientConnection();
        return cbcConn;
    }
    
	/**
	 * Creates a new NotifServer connection.
	 * 
	 */
	private IServerConnection createNotificationConn()
	{       
		NotifClientConnection notifConn = new NotifClientConnection();
		String host = globalSettingsDao.getString(GlobalSetting.NOTIFICATION_HOST);
        notifConn.setHost(host);
        int port = globalSettingsDao.getInteger(GlobalSetting.NOTIFICATION_PORT);
        notifConn.setPort(port);
		return notifConn;
	}
}