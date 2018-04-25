package com.cannontech.common.util.activemq;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;

public class ActiveMQHelper {
    private static final Logger log = YukonLogManager.getLogger(ActiveMQHelper.class);

    //  The broker name used by YukonMessageBroker.
    public static final String MESSAGE_BROKER_NAME = "YukonMessageBroker";
    
    public static URI createVmTransportUri(ApplicationId applicationId) throws URISyntaxException {
        String brokerName = resolveBrokerName(applicationId);
        
        return new URI("vm", brokerName, null, "create=false", null);
    }
    
    public static String resolveBrokerName(final ApplicationId applicationId) {
        switch (applicationId) {
            case MESSAGE_BROKER:
                return MESSAGE_BROKER_NAME;
            case WEBSERVER:
                try {
                    String hostname = InetAddress.getLocalHost().getHostName();
                    return applicationId.getApplicationName() + hostname;
                } catch (UnknownHostException e) {
                    log.info("Could not resolve host name.");
                    return applicationId.getApplicationName();
                }
            default:
                return applicationId.getApplicationName();
        }
    }
}
