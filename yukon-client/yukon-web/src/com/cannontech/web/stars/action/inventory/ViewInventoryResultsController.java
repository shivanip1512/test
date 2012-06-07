package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.InventoryBean;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ViewInventoryResultsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        InventoryBean iBean = (InventoryBean) session.getAttribute("inventoryBean");
        iBean.setViewResults(!iBean.getViewResults());
        iBean.setPage(1);
        //session.setAttribute("inventoryBean", iBean);
        
        String redirect = request.getContextPath() + "/operator/Hardware/Inventory.jsp";
        response.sendRedirect(redirect);
    }
    
}
