package com.cannontech.common.util.activemq;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.logging.log4j.Logger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class ActiveMQHelper {
    private static final Logger log = YukonLogManager.getLogger(ActiveMQHelper.class);

    //  The broker name used by YukonMessageBroker.
    private static final String MESSAGE_BROKER_NAME = "YukonMessageBroker";

    private static final List<String> CLIENT_BIN = ImmutableList.of(CtiUtilities.getYukonBase(), "Client","bin");
    private static final List<String> SERVER_WEB_BIN = ImmutableList.of(CtiUtilities.getYukonBase(), "Server", "web", "bin");
    private static final String KAHA_DB = "KahaDB";
    private static final String ACTIVEMQ_DATA = "activemq-data";
    
    private ActiveMQHelper() {
        //  hide implicit public constructor
    }
    
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

    public static File getKahaDbDirectory(ApplicationId applicationId) {
        var fullPath = String.join(File.separator, 
                                   Iterables.concat(getBasePath(applicationId),
                                                    getChildPath(applicationId)));

        return new File(fullPath);
    }

    private static List<String> getChildPath(ApplicationId applicationId) {
        return ImmutableList.of(ACTIVEMQ_DATA, resolveBrokerName(applicationId), KAHA_DB);
    }

    private static List<String> getBasePath(ApplicationId applicationId) {
        return (applicationId == ApplicationId.WEBSERVER) 
                ? SERVER_WEB_BIN 
                : CLIENT_BIN;
    }
}
