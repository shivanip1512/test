/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.integration.crs.YukonCRSIntegrator;
import com.cannontech.roles.yukon.SystemRole;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimerTaskInitializer implements ApplicationListener {
    
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            String preloadData = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
            if (CtiUtilities.isTrue( preloadData )) {
                StarsDatabaseCache.getInstance().loadData();
            }
            
            if( VersionTools.crsPtjIntegrationExists()) {	//Xcel Integration!
                YukonCRSIntegrator integrator = new YukonCRSIntegrator();
                integrator.start();
            }
        }
    }
    
    

}
