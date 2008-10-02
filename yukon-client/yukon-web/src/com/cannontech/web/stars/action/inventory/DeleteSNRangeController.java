package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.DeleteSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class DeleteSNRangeController extends StarsInventoryActionController {
	private TimeConsumingTaskService taskService;
	
	public void setTimeConsumingTaskService(final TimeConsumingTaskService taskService) {
		this.taskService = taskService;
	}
	
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        ServletUtils.saveRequest(request,
                                 session,
                                 new String[] {"Member", "From", "To", "DeviceType"} );
        
        Integer memberId = ServletRequestUtils.getIntParameter(request, "Member");
        LiteStarsEnergyCompany member = (memberId != null) ?
                this.starsDatabaseCache.getEnergyCompany(memberId) : this.starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());
        
        String fromStr = request.getParameter("From");
        String toStr = request.getParameter("To");
        Integer snFrom = null;
        Integer snTo = null;
        
        if (!fromStr.equals("*")) {
            try {
                snFrom = Integer.valueOf( fromStr );
                if (!toStr.equals("*")) {
                    snTo = Integer.valueOf( toStr );
                    if (snFrom.intValue() > snTo.intValue()) {
                        session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'From' value is greater than the 'To' value");
                        String redirect = this.getReferer(request);
                        response.sendRedirect(redirect);
                        return;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the SN range");
                String redirect = this.getReferer(request);
                response.sendRedirect(redirect);
                return;
            }
        }
        
        Integer devTypeID = ServletRequestUtils.getIntParameter(request, "DeviceType");
        
        session.removeAttribute( ServletUtils.ATT_REDIRECT );
        
        boolean confirmOnMessagePage = request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null;
        String redirectUrl = ServletUtil.createSafeRedirectUrl(request, "/operator/Hardware/ResultSet.jsp");
        
        TimeConsumingTask task = new DeleteSNRangeTask( member, snFrom, snTo, 
            devTypeID, confirmOnMessagePage, redirectUrl, session);

        String redirect = this.getRedirect(request);
        String referer = this.getReferer(request);
        
        String location = this.taskService.processTask(task, request, redirect, referer);
        response.sendRedirect(location);
    }
    
}
