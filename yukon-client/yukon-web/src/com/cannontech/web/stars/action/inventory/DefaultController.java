package com.cannontech.web.stars.action.inventory;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class DefaultController extends StarsInventoryActionController  {

    @SuppressWarnings("unchecked")
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        String redirect = this.getRedirect(request);
        List filterList = (List) session.getAttribute(ServletUtils.FILTER_INVEN_LIST);
        
        if (redirect.compareTo("/operator/Hardware/Inventory.jsp") == 0 && (filterList == null || filterList.size() < 1)) {
            redirect = "/operator/Hardware/Filter.jsp";
        }
        
        response.sendRedirect( redirect );
    }

}

