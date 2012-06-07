package com.cannontech.web.stars.action.inventory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsInventoryActionController;

public class CreateLMHardwareController extends StarsInventoryActionController {
    private Controller checkInventory;
    
    public void setCheckInventory(final Controller checkInventroy) {
        this.checkInventory = checkInventroy;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        final String action = ServletRequestUtils.getStringParameter(request, "action");
        final String wizard = ServletRequestUtils.getStringParameter(request, "Wizard");
        String redirect = request.getContextPath() + "/servlet/SOAPClient?action=" + action; 
        
        if (wizard != null) redirect += "&Wizard=true";
        
        session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
        
        this.checkInventory.handleRequest(request, response);
    }

}
