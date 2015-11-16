package com.cannontech.web.stars.action.workorder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class ManipulateResultsController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = request.getContextPath() + "/operator/WorkOrder/ChangeWorkOrders.jsp";
        response.sendRedirect(redirect);
    }
    
}
