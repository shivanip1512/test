package com.cannontech.web.stars.action.workorder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateServiceRequestAction;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SearchCustAccountController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = request.getContextPath() + "/servlet/SOAPClient?action=UpdateWorkOrder" +
        "&REDIRECT=" + request.getParameter(ServletUtils.ATT_REDIRECT) +
        "&REFERRER=" + request.getParameter(ServletUtils.ATT_REFERRER);
        
        LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchAccountByAccountNo( request.getParameter("AcctNo") );
        if (liteAcctInfo == null) {
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "The specified account # doesn't exist");
            String location = this.getReferer(request);
            response.sendRedirect(location);
            return;
        }
        
        StarsOperation operation = null;
        try {
            operation = UpdateServiceRequestAction.getRequestOperation(request, energyCompany.getDefaultTimeZone());
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
