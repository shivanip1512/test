package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.util.task.UpdateSNRangeTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class UpdateSNRangeController extends StarsInventoryActionController {
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
                                 new String[] {"Member", "From", "To", "DeviceType", "NewDeviceType", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );

        Integer memberId = ServletRequestUtils.getIntParameter(request, "Member");
        LiteStarsEnergyCompany member = (memberId != null) ?
                this.starsDatabaseCache.getEnergyCompany(memberId) : this.starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());
        
        Integer devTypeID = ServletRequestUtils.getIntParameter(request, "DeviceType");
        Integer newDevTypeID = ServletRequestUtils.getIntParameter(request, "NewDeviceType");
        
        if (newDevTypeID != null && newDevTypeID.intValue() == devTypeID.intValue()) {
            newDevTypeID = null;
        }
        
        long snFrom = 0, snTo = 0;
        try {
            snFrom = Long.parseLong( request.getParameter("From") );
            if (request.getParameter("To").length() > 0)
                snTo = Long.parseLong( request.getParameter("To") );
            else
                snTo = snFrom;
        }
        catch (NumberFormatException nfe) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the Serial range");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }
        if (snFrom > snTo) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The Serial range 'from' value cannot be greater than the 'to' value");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }

        Integer voltageID = ServletRequestUtils.getIntParameter(request, "Voltage");
        Integer companyID = ServletRequestUtils.getIntParameter(request, "ServiceCompany");
        Integer routeID = ServletRequestUtils.getIntParameter(request, "Route");

        Date recvDate = null;
        String recvDateStr = request.getParameter("ReceiveDate");
        if (recvDateStr != null && recvDateStr.length() > 0) {
            recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, member.getDefaultTimeZone());
            if (recvDate == null) {
                session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
                String redirect = this.getReferer(request);
                response.sendRedirect(redirect);
                return;
            }
        }

        if (newDevTypeID == null && recvDate == null && voltageID == null && companyID == null && routeID == null) {
            String redirect = this.getRedirect(request);
            response.sendRedirect(redirect);
            return;
        }
            
        session.removeAttribute( ServletUtils.ATT_REDIRECT );

        boolean confirmOnMessagePage = request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null;
        String redirectUrl = ServletUtil.createSafeRedirectUrl(request, "/operator/Hardware/ResultSet.jsp");
        
        TimeConsumingTask task = new UpdateSNRangeTask( member, snFrom, snTo, devTypeID, 
            newDevTypeID, recvDate, voltageID, companyID, routeID, confirmOnMessagePage, redirectUrl, session);
        
        String redirect = this.getRedirect(request);
        String referer = this.getReferer(request);
        
        String location = this.taskService.processTask(task, request, redirect, referer);
        response.sendRedirect(location);
    }

}
