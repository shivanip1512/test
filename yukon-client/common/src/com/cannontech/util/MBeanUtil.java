package com.cannontech.util;

import java.util.Map;

import javax.management.*;
import javax.management.remote.*;
import javax.naming.Context;

import com.cannontech.clientutils.CTILogger;

public class MBeanUtil {
    static private MBeanServer server;
    
    static {
        //RMI specific setters
        System.setProperty( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory" );
        System.setProperty( Context.PROVIDER_URL, "rmi://localhost:1099" );
        
        server = MBeanServerFactory.createMBeanServer("cti.yukon");
        
        try {
            ObjectName naming = new ObjectName("Naming:type=registry");
            //  Create and register the MBean in the MBeanServer
            server.createMBean("mx4j.tools.naming.NamingService", naming, null);            
            server.invoke(naming, "start", null, null);

   
//       The address of the connector server
            JMXServiceURL address = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/server");

//       The environment map, null in this case
            Map environment = null;

//       Create the JMXCconnectorServer
            JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(address, environment, server);

//       Start the JMXConnectorServer
            cntorServer.start();
        } catch (Exception e) {
            CTILogger.warn("Could not add RMI Connector and naming service to MBeanServer", e);
        }

    }

    static public MBeanServer getInstance() {
        return server;
    }
    
    /**
     * This method wraps up the process of creating a name and registering the object.
     * It does not throw any exceptions because, at this point, MBean aren't required
     * for the system to work correctly. Any errors will be logged as warnings.
     * 
     * @param keyValuePairs The "name1=value,name2=value" that comes after the colon
     * @param object The object to register
     */
    static public void tryRegisterMBean(String keyValuePairs, Object object) {
        try {
            // do some weird mbean stuff
            ObjectName name = new ObjectName(":" + keyValuePairs);

            MBeanUtil.getInstance().registerMBean(object, name);
        } catch (Exception e) {
            CTILogger.warn("Unable to register " + object.getClass().getName() + " as " + keyValuePairs, e);
        }
    }
}
