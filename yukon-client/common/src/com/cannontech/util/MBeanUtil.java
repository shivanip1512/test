package com.cannontech.util;

import java.net.BindException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.BootstrapUtils;

/**
 * Utility class. Creates an rmiregistry at port 1099, creates an MBeanServer
 * and creates and starts a JMXConnectorServer for the MBeanServer at address:
 * service:jmx:rmi:///jndi/rmi://localhost:1099/{application name}
 */
public class MBeanUtil {
    private static MBeanServer server;
    private static final String jmxUrl = "service:jmx:rmi:///jndi/rmi://localhost:1099/";

    static {

        // RMI specific setters
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                           "com.sun.jndi.rmi.registry.RegistryContextFactory");
        System.setProperty(Context.PROVIDER_URL, "rmi://localhost:1099");

        server = MBeanServerFactory.createMBeanServer("com.cannontech.yukon");

        try {

            // Create the rmi registry for connector server registration on port
            // 1099
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(1099);
            } catch (ExportException e) {
                // Ignore BindException - this means the rmi registry has
                // already been started. Throw anything else.
                if (!(e.getCause() instanceof BindException)) {
                    throw e;
                }
                registry = LocateRegistry.getRegistry(1099);
                System.out.println("registry already there: ");
            }

            // Unbind the connector server for the current application if one
            // exists
            try {
                registry.unbind(BootstrapUtils.getApplicationName());
            } catch (NotBoundException e) {
                // ignore - only unbind if exists
            }

            // The address of the connector server
            JMXServiceURL address = new JMXServiceURL(jmxUrl + BootstrapUtils.getApplicationName());

            // The environment map, null in this case
            Map<String, ?> environment = null;

            // Create the JMXCconnectorServer
            JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(address,
                                                                                             environment,
                                                                                             server);

            // Start the JMXConnectorServer
            cntorServer.start();

        } catch (Exception ex) {
            CTILogger.warn("Could not add RMI Connector and naming service to MBeanServer", ex);
        }

    }

    static public MBeanServer getInstance() {
        return server;
    }

    /**
     * This method wraps up the process of creating a name and registering the
     * object. It does not throw any exceptions because, at this point, MBean
     * aren't required for the system to work correctly. Any errors will be
     * logged as warnings.
     * @param keyValuePairs The "name1=value,name2=value" that comes after the
     *            colon
     * @param object The object to register
     */
    static public void tryRegisterMBean(String keyValuePairs, Object object) {
        try {
            // do some weird mbean stuff
            ObjectName name = new ObjectName(":" + keyValuePairs);

            MBeanUtil.getInstance().registerMBean(object, name);
        } catch (Exception e) {
            CTILogger.warn("Unable to register " + object.getClass().getName() + " as " + keyValuePairs,
                           e);
        }
    }
}
