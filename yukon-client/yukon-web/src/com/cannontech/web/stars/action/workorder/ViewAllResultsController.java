package com.cannontech.web.stars.action.workorder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.bean.WorkOrderBean;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class ViewAllResultsController extends StarsWorkorderActionController {
    private boolean viewAll;
    
    public void setViewAll(final boolean viewAll) {
        this.viewAll = viewAll;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        workOrderBean.setViewAllResults(this.viewAll);
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
