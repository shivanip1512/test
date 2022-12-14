/*
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.LMControlHistoryUtil;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class DailyTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 60 * 24;	// 1 day

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#isFixedRate()
	 */
	@Override
    public boolean isFixedRate() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#getTimerPeriod()
	 */
	@Override
    public long getTimerPeriod() {
		return TIMER_PERIOD;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.util.task.StarsTimerTask#getNextScheduledTime()
	 */
	@Override
    public Date getNextScheduledTime() {
		return ServletUtil.getTomorrow();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
    public void run() {
		CTILogger.debug( "*** Daily timer task start ***" );
		String batchProcessType =  YukonSpringHook.getBean(GlobalSettingDao.class).getString(GlobalSettingType.BATCHED_SWITCH_COMMAND_TOGGLE);
        boolean noAuto = false;
        if(batchProcessType != null)
        {
            noAuto = batchProcessType.compareTo(StarsUtils.BATCH_SWITCH_COMMAND_MANUAL) == 0; 
        }
        
        if(noAuto)
        {
            CTILogger.info( "Auto processing of batch commands currently disabled." );
        }
        else
        {
            // Clear all the *active* control history
    		LMControlHistoryUtil.clearActiveControlHistory();
    		
            List<LiteStarsEnergyCompany> companies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
    		if (companies == null) return;
    		
    		for (int i = 0; i < companies.size(); i++) 
            {
    			LiteStarsEnergyCompany company = companies.get(i);
    			if (ECUtils.isDefaultEnergyCompany( company )) continue;
    			
    			SwitchCommandQueue.SwitchCommand[] commands = SwitchCommandQueue.getInstance().getCommands( company.getLiteID(), false );
    			if (commands != null && commands.length > 0) {
    				int numCmdSent = 0;
    				for (int j = 0; j < commands.length; j++) {
    					try {
    						InventoryManagerUtil.sendSwitchCommand( commands[j] );
    						numCmdSent++;
    					} catch (CommandCompletionException e) {
    						CTILogger.debug( e.getMessage() );
    					}
    				}
    				
    				String msg = numCmdSent + " of " + commands.length + " switch commands sent successfully";
    				ActivityLogger.logEvent(YukonUserContext.system.getYukonUser().getUserID(), -1, company.getLiteID(), -1, ActivityLogActions.HARDWARE_SEND_BATCH_CONFIG_ACTION, msg);
    				
                    Map<Integer,Object[]> batchConfig = InventoryManagerUtil.getBatchConfigSubmission();
    				batchConfig.put( company.getEnergyCompanyId(), new Object[]{new Date(), msg} );
    			}
    		}
        }
        
		CTILogger.debug( "*** Daily timer task stop ***" );
	}

}
