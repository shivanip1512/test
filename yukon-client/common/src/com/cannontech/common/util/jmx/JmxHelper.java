package com.cannontech.common.util.jmx;

import com.cannontech.common.util.ApplicationId;
import com.google.common.collect.ImmutableMap;

public class JmxHelper {
    private static ImmutableMap<ApplicationId, Integer> jmxPorts;
    
    static {
        ImmutableMap.Builder<ApplicationId, Integer> builder = ImmutableMap.builder();
        
        builder.put(ApplicationId.SERVICE_MANAGER,    1099);
        builder.put(ApplicationId.WEBSERVER,          1098);
        builder.put(ApplicationId.MESSAGE_BROKER,     1097);
        builder.put(ApplicationId.SIMULATORS_SERVICE, 1096);
        builder.put(ApplicationId.NOTIFICATION,       1095);

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
