package com.cannontech.common.util.jmx;

import com.cannontech.common.util.ApplicationId;
import com.google.common.collect.ImmutableMap;

public class JmxHelper {
    private static ImmutableMap<ApplicationId, Integer> jmxPorts;
    
    static {
        ImmutableMap.Builder<ApplicationId, Integer> builder = ImmutableMap.builder();

        //builder.put(ApplicationId.NETWORK_MANAGER, 1100); NM is using port 1100.
        
        //Port 1099 is the default, and is best avoided.
        builder.put(ApplicationId.WEBSERVER, 1098);
        builder.put(ApplicationId.MESSAGE_BROKER, 1097);
        builder.put(ApplicationId.SIMULATORS_SERVICE, 1096);
        builder.put(ApplicationId.NOTIFICATION, 1095);
        // JMX for watchdog is set in watchdog.conf wrapper file.
        //builder.put(ApplicationId.WATCHDOG, 1094);
        builder.put(ApplicationId.SERVICE_MANAGER, 1093);

        jmxPorts = builder.build();
    }
    
    /**
     * Returns the JMX port associated with the specified application, or null if none.
     * @param app The application to check.
     * @return the JMX port, or null if none.
     */
    public static Integer getApplicationJmxPort(ApplicationId app) {
        return jmxPorts.get(app);
    }
}
