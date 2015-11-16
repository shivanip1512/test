package com.cannontech.web.stars.action.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class RemoveSwitchCommandsController extends StarsInventoryActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        if (request.getParameter("All") != null) {
            int memberID = Integer.parseInt(request.getParameter("All"));
            
            List<LiteStarsEnergyCompany> descendants = ECUtils.getAllDescendants( energyCompany );
            for (int i = 0; i < descendants.size(); i++) {
                LiteStarsEnergyCompany company = descendants.get(i);
                if (memberID >= 0 && company.getLiteID() != memberID) continue;
                
                SwitchCommandQueue.getInstance().clearCommands( company.getLiteID() );
            }
        }
        else {
            String[] values = request.getParameterValues( "InvID" );
            for (int i = 0; i < values.length; i++)
                SwitchCommandQueue.getInstance().removeCommand( Integer.parseInt(values[i]) );
        }
        
        session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Switch commands removed successfully");
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
