package com.cannontech.web.stars.action.workorder;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.web.action.UpdateServiceRequestAction;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SearchCustAccountController extends StarsWorkorderActionController {

    @Autowired private EnergyCompanyService ecService;
    @Autowired private StarsSearchService starsSearchService;

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) 
                    throws Exception {
        
        String redirect = request.getContextPath() + "/servlet/SOAPClient?action=UpdateWorkOrder" +
        "&REDIRECT=" + request.getParameter(ServletUtils.ATT_REDIRECT) +
        "&REFERRER=" + request.getParameter(ServletUtils.ATT_REFERRER);
        
        LiteAccountInfo liteAcctInfo = 
                starsSearchService.searchAccountByAccountNo(energyCompany, request.getParameter("AcctNo"));
        if (liteAcctInfo == null) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified account # doesn't exist");
            String location = this.getReferer(request);
            response.sendRedirect(location);
            return;
        }
        
        StarsOperation operation = null;
        try {
            TimeZone systemTimeZone = ecService.getDefaultTimeZone(energyCompany.getEnergyCompanyId());
            operation = UpdateServiceRequestAction.getRequestOperation(request, systemTimeZone);
        }
        catch (WebClientException se) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, se.getMessage());
            String location = this.getReferer(request);
            response.sendRedirect(location);
            return;
        }
        
        operation.getStarsUpdateServiceRequest().setAccountID( liteAcctInfo.getAccountID() );
        session.setAttribute(WorkOrderManagerUtil.STARS_WORK_ORDER_OPER_REQ, operation);
        response.sendRedirect(redirect);
    }
    
}
