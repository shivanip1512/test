package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ConfigSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class ConfigSNRangeController extends StarsInventoryActionController {
	private TimeConsumingTaskService taskService;
	
	public void setTimeConsumingTaskService(final TimeConsumingTaskService taskService) {
		this.taskService = taskService;
	}
	
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
    	boolean configNow = request.getParameter("ConfigNow") != null;

    	session.removeAttribute( ServletUtils.ATT_REDIRECT );

    	TimeConsumingTask task = new ConfigSNRangeTask( energyCompany, configNow, request );
    	
    	String redirect = this.getRedirect(request);
    	String referer = this.getReferer(request);
    	
    	String location = this.taskService.processTask(task, request, redirect, referer);
    	response.sendRedirect(location);
    }
    
}
