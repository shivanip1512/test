package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ConfigSNRangeTask;
import com.cannontech.stars.util.task.ConfigSNRangeTaskDTO;
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
        
    	session.removeAttribute( ServletUtils.ATT_REDIRECT );
    	
    	ConfigSNRangeTaskDTO dto = ConfigSNRangeTaskDTO.valueOf(request);
    	
    	TimeConsumingTask task = new ConfigSNRangeTask(energyCompany, session, dto);
    	
    	String redirect = this.getRedirect(request);
    	String referer = this.getReferer(request);
    	
    	String location = this.taskService.processTask(task, request, redirect, referer);
    	response.sendRedirect(location);
    }
    
}
