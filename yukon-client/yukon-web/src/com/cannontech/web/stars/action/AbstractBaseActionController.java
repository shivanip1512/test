package com.cannontech.web.stars.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;

public abstract class AbstractBaseActionController implements Controller {
    public final static String LOGIN_URL = "/login.jsp";

    @Autowired private StarsDatabaseCache starsDatabaseCache;

    public abstract String getRedirect(HttpServletRequest request) throws Exception;
    
    public abstract String getReferer(HttpServletRequest request) throws Exception;
    
    public abstract void doAction(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            StarsYukonUser user, LiteStarsEnergyCompany energyCompany) throws Exception;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        StarsYukonUser user = ServletUtils.getStarsYukonUser(request);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(user.getEnergyCompanyID());

        doAction(request, response, session, user, energyCompany);
        return null;
    }
}
