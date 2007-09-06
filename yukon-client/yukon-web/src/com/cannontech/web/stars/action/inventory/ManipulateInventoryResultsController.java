package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class ManipulateInventoryResultsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = request.getContextPath() + "/operator/Hardware/ChangeInventory.jsp";
        response.sendRedirect(redirect);
    }
    
}
