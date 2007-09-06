package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.stars.action.AbstractBaseActionController;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class RefreshCacheController extends StarsAdminActionController {

    @Override
    public void doAction(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            StarsYukonUser user, LiteStarsEnergyCompany energyCompany) throws Exception {

        final String range = ServletRequestUtils.getStringParameter(request, "Range");
        
        if (range.equalsIgnoreCase("All")) {
            this.starsDatabaseCache.refreshCache();
        }
        
        if (range.equalsIgnoreCase("EnergyCompany")) {
            this.starsDatabaseCache.refreshCache(energyCompany);
        }
        
        session.invalidate();
        String redirect = request.getContextPath() + AbstractBaseActionController.LOGIN_URL;
        response.sendRedirect(redirect); 
    }

}
