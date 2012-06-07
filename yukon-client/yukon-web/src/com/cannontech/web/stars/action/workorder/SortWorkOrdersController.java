package com.cannontech.web.stars.action.workorder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class SortWorkOrdersController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        
        String sortBy = ServletRequestUtils.getStringParameter(request, "SortBy");
        String sortOrder = ServletRequestUtils.getStringParameter(request, "SortOrder");
        
        if (sortBy != null) workOrderBean.setSortBy(Integer.parseInt(sortBy));
        
        if(sortOrder != null) workOrderBean.setSortOrder(Integer.parseInt(sortOrder));
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
