package com.cannontech.services.server;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

public class YukonServicesServer {

    public static void main(String[] args) {
        CtiUtilities.setDefaultApplicationName("ServiceManager");
        YukonSpringHook.setDefaultContext(YukonSpringHook.SERVICES_BEAN_FACTORY_KEY);
        Logger log = YukonLogManager.getLogger(YukonServicesServer.class);
        try {

            //Assume the default server login operation
            //            ClientSession session = ClientSession.getInstance(); 
            //            if(!session.establishDefServerSession())
            //                System.exit(-1);          
            //                
            //            if(session == null) 
            //                System.exit(-1);

            YukonServiceManager serviceManager = YukonSpringHook.getBean(YukonServiceManager.class);
            serviceManager.loadCustomServices();
            serviceManager.waitForShutdown();
            log.info("main thread done");
        } catch( Throwable t ) {
            log.error( "Problem with loading services", t);
        }

    }


}
