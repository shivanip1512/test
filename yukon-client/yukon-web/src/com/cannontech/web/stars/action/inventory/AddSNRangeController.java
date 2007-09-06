package com.cannontech.web.stars.action.inventory;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.task.AddSNRangeTask;
import com.cannontech.stars.util.task.TimeConsumingTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;
import com.cannontech.web.stars.service.TimeConsumingTaskService;

public class AddSNRangeController extends StarsInventoryActionController {
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
                                 new String[] {"Member", "From", "To", "DeviceType", "DeviceState", "ReceiveDate", "Voltage", "ServiceCompany", "Route"} );

        Integer memberId = ServletRequestUtils.getIntParameter(request, "Member");
        LiteStarsEnergyCompany member = (memberId != null) ?
                this.starsDatabaseCache.getEnergyCompany(memberId) : this.starsDatabaseCache.getEnergyCompany( user.getEnergyCompanyID());

        int snFrom = 0, snTo = 0;
        try {
            snFrom = Integer.parseInt( request.getParameter("From") );
            if (request.getParameter("To").length() > 0)
                snTo = Integer.parseInt( request.getParameter("To") );
            else
                snTo = snFrom;
        }
        catch (NumberFormatException nfe) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid number format in the SN range");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }

        if (snFrom > snTo) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The 'from' value cannot be greater than the 'to' value");
            String redirect = this.getReferer(request);
            response.sendRedirect(redirect);
            return;
        }

        Integer devTypeID = ServletRequestUtils.getIntParameter(request, "DeviceType");
        Integer devStateID = ServletRequestUtils.getIntParameter(request, "DeviceState");
        Integer voltageID = ServletRequestUtils.getIntParameter(request, "Voltage");
        Integer companyID = ServletRequestUtils.getIntParameter(request, "ServiceCompany");
        Integer routeID = ServletRequestUtils.getIntParameter(request, "Route");

        /*
        Date recvDate = null;
        String recvDateStr = req.getParameter("ReceiveDate");
                          if (recvDateStr.length() > 0) {
                              recvDate = com.cannontech.util.ServletUtil.parseDateStringLiberally(recvDateStr, member.getDefaultTimeZone());
                              if (recvDate == null) {
                                  session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid receive date format");
                                  redirect = referer;
                                  return;
                              }
                          }*/

        // if operation completed, but not all serial numbers added, show the result on "ResultSet.jsp"
        // (the REDIRECT parameter is set within the AddSNRangeTask.run() method)
        session.removeAttribute( ServletUtils.ATT_REDIRECT );

        TimeConsumingTask task = new AddSNRangeTask( member, snFrom, snTo, devTypeID, devStateID, new Date(), voltageID, companyID, routeID, request );
        
        String redirect = this.getRedirect(request);
        String referer = this.getReferer(request);
        
        String location = this.taskService.processTask(task, request, redirect, referer);
        response.sendRedirect(location);
    }
    
}
