package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;
import com.cannontech.web.stars.service.SwitchContextService;

public class SwitchContextController extends StarsAdminActionController {
    private SwitchContextService switchContextService;
    
    public void setSwitchContextService(final SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        try {
            int memberID = ServletRequestUtils.getIntParameter(request, "MemberID");
            this.switchContextService.switchContext(user, request, session, memberID);
        }
        catch (WebClientException e) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
            String referer = this.getReferer(request);
            response.sendRedirect(referer);
            return;
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
}
