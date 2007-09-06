package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.ConfigSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.stars.web.util.InventoryManagerUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class ConfigureResultSetController extends StarsInventoryActionController {
	private TimeConsumingTaskService taskService;
	
	public void setTimeConsumingTaskService(final TimeConsumingTaskService taskService) {
		this.taskService = taskService;
	}
	
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = this.getRedirect(request);
        String referer = this.getReferer(request);
        
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        if(iBean.getInventoryList() != null && iBean.getInventoryList().size() > 0) {
            session.setAttribute(InventoryManagerUtil.SN_RANGE_TO_CONFIG, iBean.getInventoryList());

    		boolean configNow = request.getParameter("ConfigNow") != null;
    		session.removeAttribute( ServletUtils.ATT_REDIRECT );
    		
            TimeConsumingTask task = new ConfigSNRangeTask( energyCompany, configNow, request);
            redirect = this.taskService.processTask(task, request, redirect, referer);
            session.setAttribute(ServletUtils.ATT_REDIRECT, request.getContextPath() + "/operator/Hardware/Inventory.jsp");
        } else {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "No inventory results are available to configure.");
            redirect = request.getContextPath() + "/operator/Hardware/Inventory.jsp";
        }
        
        response.sendRedirect(redirect);
    }
    
}
