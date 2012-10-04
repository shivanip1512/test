/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import javax.servlet.ServletContextEvent;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.integration.crs.YukonCRSIntegrator;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.web.util.TimerTaskUtil;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.impl.GlobalSettingsDaoImpl;

public class TimerTaskInitializer extends ErrorAwareContextListener {
    
    @Override
    public void doContextInitialized(ServletContextEvent sce) {
        TimerTaskUtil ttu = (TimerTaskUtil) getApplicationContext().getBean("timerTaskUtil");
        ttu.startAllTimerTasks();
        
        String preloadData = YukonSpringHook.getBean("globalSettingsDao",GlobalSettingsDaoImpl.class).getString(GlobalSetting.STARS_PRELOAD_DATA);
        if (CtiUtilities.isTrue( preloadData )) {
            StarsDatabaseCache.getInstance().loadData();
        }
        
        if( VersionTools.crsPtjIntegrationExists()) {   //Xcel Integration!
            YukonCRSIntegrator integrator = new YukonCRSIntegrator();
            integrator.start();
        }
    }

}
