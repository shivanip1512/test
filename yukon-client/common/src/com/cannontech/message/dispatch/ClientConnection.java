package com.cannontech.message.dispatch;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.SystemUserContext;
import com.roguewave.vsj.CollectableStreamer;

public class ClientConnection extends com.cannontech.message.util.ClientConnection {
    public ClientConnection() {
        super("Dispatch");
    }

    /**
     * @param host java.lang.String
     * @param port int
     */
    @Deprecated
    public ClientConnection(String host, int port) {
        super(host, port);
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof Command && ((Command) msg).getOperation() == Command.ARE_YOU_THERE) {
            // Only instances of com.cannontech.message.dispatch.message.Command
            // should
            // get here and it should have a ARE_YOU_THERE operation
            // echo it back so vangogh doesn't time out on us

            write(msg);
        }

        super.fireMessageEvent(msg);
    }

    protected void registerMappings(CollectableStreamer polystreamer) {
        super.registerMappings(polystreamer);

        com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();

        for (int i = 0; i < mappings.length; i++) {
            polystreamer.register(mappings[i]);
        }

        // polystreamer.register()
    }

    /**
     * Returns a ClientConnection to dispatch. The connection is returned in an
     * unconnected state. Which means you can modify it (add observers, for instance)
     * and then call your favorite connect*() method.
     * @param applicationName The application name passed to Registration.setAppName()
     * @return an unconnected ClientConnection
     */
    public static ClientConnection createDefaultConnection(String applicationName) {
        String defaultHost = "127.0.0.1";
        int defaultPort = 1510;

        try {
            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
            defaultHost = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DISPATCH_MACHINE, new SystemUserContext().getYukonUser());
            defaultPort = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.DISPATCH_PORT, new SystemUserContext().getYukonUser());
        } catch (Exception e) {
            CTILogger.warn("Could not get host and port for dispatch connection from Role Properties, using defaults",
                           e);
        }

        ClientConnection connToDispatch = new ClientConnection();
        Registration reg = new Registration();
        reg.setAppName(applicationName);
        reg.setAppIsUnique(0);
        reg.setAppKnownPort(0);
        reg.setAppExpirationDelay(300); // 5 minutes should be OK

        connToDispatch.setHost(defaultHost);
        connToDispatch.setPort(defaultPort);
        connToDispatch.setAutoReconnect(true);
        connToDispatch.setRegistrationMsg(reg);

        return connToDispatch;
    }

    public static ClientConnection createDefaultConnection() {
        return createDefaultConnection(CtiUtilities.getAppRegistration());
    }

}
