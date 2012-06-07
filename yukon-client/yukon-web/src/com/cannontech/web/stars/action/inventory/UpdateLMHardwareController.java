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

public class UpdateLMHardwareController extends StarsInventoryActionController {
    private Controller checkInventory;

    public void setCheckInventory(final Controller checkInventory) {
        this.checkInventory = checkInventory;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        final String action = ServletRequestUtils.getStringParameter(request, "action");
        final String paramRedirect = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT);
        final String paramReferer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER);
        
        final StringBuilder sb = new StringBuilder();
        sb.append(request.getContextPath() + "/servlet/SOAPClient?action=" + action);
        sb.append("&REDIRECT=" + paramRedirect);
        sb.append("&REFERRER=" + paramReferer);
        
        String redirect = sb.toString();
        session.setAttribute( ServletUtils.ATT_REDIRECT, redirect );
        
        this.checkInventory.handleRequest(request, response);
    }
    
}
