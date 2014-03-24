package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.StarsAdminActionController;
import com.cannontech.web.stars.service.SwitchContextService;

public class SwitchContextController extends StarsAdminActionController {
    @Autowired private SwitchContextService switchContextService;

    @Override
    public void doAction(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            StarsYukonUser user, LiteStarsEnergyCompany energyCompany) throws Exception {
        try {
            int memberID = ServletRequestUtils.getIntParameter(request, "MemberID");
            switchContextService.switchContext(user, request, session, memberID);
        }
        catch (WebClientException e) {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
            String referer = getReferer(request);
            response.sendRedirect(referer);
            return;
        }
        
        String redirect = getRedirect(request);
        response.sendRedirect(redirect);
    }
}
