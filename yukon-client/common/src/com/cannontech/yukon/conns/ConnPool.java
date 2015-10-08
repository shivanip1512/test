package com.cannontech.yukon.conns;

import java.util.Hashtable;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.ClientConnectionFactory;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.INotifConnection;
import com.cannontech.yukon.IServerConnection;

/**
 * Factory and container for connections to all Yukon servers.  Despite its name, this is not a pool of any sort.
 */
public class ConnPool {
    // default keys that are used in the pool. New connections
    // will be made based on the type found in the given string.
    // This process is not case sensitive.
    //
    //  example: TDC_PORTER creates a new Porter type connection
    //           with the given String as its name
    public static final String PORTER_CONN = "PORTER";
    public static final String DISPATCH_CONN = "DISPATCH";
    public static final String MACS_CONN = "MACS";
    public static final String CAPCONTROL_CONN = "CAPCONTROL";
    public static final String NOTIFCATION_CONN = "NOTIFCATION";

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ClientConnectionFactory clientConnectionFactory;

    private Hashtable<String, IServerConnection> _allConns = null;

    /**
     * Get the singleton instance of this class.
     * @deprecated Inject using Spring or call {@link YukonSpringHook#getBean(Class)} directly.
     */
    @Deprecated
    public static synchronized ConnPool getInstance() {
        return YukonSpringHook.getBean(ConnPool.class);
    }

    private Hashtable<String, IServerConnection> getAllConns() {
        if (_allConns == null) {
            _allConns = new Hashtable<String, IServerConnection>(16);
        }

        return _allConns;
    }

    @PreDestroy
    public void shutdown() {
        for (IServerConnection conn : _allConns.values()) {
            conn.disconnect();
        }
    }

    /**
     * Create a new Porter connection.
     */
    private IServerConnection createPorterConn() {
        return clientConnectionFactory.createPorterConn();
    }

    /**
     * Create a new Dispatch connection.
     */
    private DispatchClientConnection createDispatchConn() {
        DispatchClientConnection connToDispatch = clientConnectionFactory.createDispatchConn();

        Registration reg = new Registration();
        // App name will be value of cti.app.name system property.
        reg.setAppName(BootstrapUtils.getApplicationName());
        reg.setAppIsUnique(0);
        reg.setAppKnownPort(0);
        reg.setAppExpirationDelay(900); // 15 minutes should be OK

        connToDispatch.setRegistrationMsg(reg);

        return connToDispatch;
    }

    /**
     * Get a ClientConnection to dispatch. The connection is returned in an
     * connected state. Notice that no PointRegistration is done here.
     */
    public DispatchClientConnection getDefDispatchConn() {
        // check our master Map of existing connections
        DispatchClientConnection connToDispatch = (DispatchClientConnection) getAllConns().get(DISPATCH_CONN);

        if (connToDispatch == null) {
            connToDispatch = createDispatchConn();

            CTILogger.info("Attempting Dispatch connection to " + connToDispatch.toString());
            connToDispatch.connectWithoutWait();

            getAllConns().put(DISPATCH_CONN, connToDispatch);
        }

        return connToDispatch;
    }

    /**
     * Get the default Porter connection that is available to all users.
     * If not found, we create a default connection that tries connecting
     * right away.
     */
    public IServerConnection getDefPorterConn() {
        // check our master Map of existing connections
        com.cannontech.message.porter.PorterClientConnection porterCC =
            (com.cannontech.message.porter.PorterClientConnection) getAllConns().get(PORTER_CONN);

        if (porterCC == null) {
            porterCC = (com.cannontech.message.porter.PorterClientConnection) createPorterConn();

            CTILogger.info("Attempting Porter connection to " + porterCC.toString());
            porterCC.connectWithoutWait();

            getAllConns().put(PORTER_CONN, porterCC);
        }

        return porterCC;

    }

    /**
     * Gets the default MACS connection that is available to all users.
     */
    public IServerConnection getDefMacsConn() {
        // check our master Map of existing connections
        ServerMACSConnection macsConn = (ServerMACSConnection) getAllConns().get(MACS_CONN);

        if (macsConn == null) {
            macsConn = (ServerMACSConnection) createMacsConn();

            macsConn.setRegistrationMsg(macsConn.getRetrieveAllSchedulesMsg());

            CTILogger.info("Attempting MACS connection to " + macsConn.toString());
            macsConn.connectWithoutWait();

            getAllConns().put(MACS_CONN, macsConn);
        }

        return macsConn;
    }

    /**
     * Creates a new MACS connection.
     */
    private IMACSConnection createMacsConn() {
        return clientConnectionFactory.createMacsConn();
    }

    /**
     * Gets the default CapControl connection that is available to all users.
     */
    public IServerConnection getDefCapControlConn() {
        // check our master Map of existing connections
        CapControlClientConnection cbcConn = (CapControlClientConnection) getAllConns().get(CAPCONTROL_CONN);

        if (cbcConn == null) {
            cbcConn = (CapControlClientConnection) createCapControlConn();

            cbcConn.connectWithoutWait();

            getAllConns().put(CAPCONTROL_CONN, cbcConn);
        }

        return cbcConn;
    }

    /**
     * Gets the default NotifcationClient connection that is available to all users.
     * 
     */
    public INotifConnection getDefNotificationConn() {
        // check our master Map of existing connections
        NotifClientConnection notifConn = (NotifClientConnection) getAllConns().get(NOTIFCATION_CONN);

        if (notifConn == null) {
            notifConn = (NotifClientConnection) createNotificationConn();

            notifConn.connectWithoutWait();

            getAllConns().put(NOTIFCATION_CONN, notifConn);
        }

        return notifConn;
    }

    /**
     * Creates a new CapControl connection.
     */
    private IServerConnection createCapControlConn() {
        return clientConnectionFactory.createCapControlConn();
    }

    /**
     * Creates a new NotifServer connection.
     */
    private IServerConnection createNotificationConn() {
        return clientConnectionFactory.createNotificationConn();
    }
}
