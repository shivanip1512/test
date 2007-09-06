package com.cannontech.web.stars.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.servlet.LoginController;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.action.StarsAdminActionController;

public class MemberLoginController extends StarsAdminActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, 
                final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        //TEM is there a role we can check loggedInUser for???
        int userID = ServletRequestUtils.getIntParameter(request, "UserID");
        LiteYukonUser memberLogin = this.yukonUserDao.getLiteYukonUser( userID );
        boolean isMemberManager = false;
        
        /*
         * This is for energy company member management in STARS, and oh yes,
         * it is rather hackish.  w00t.
         */
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        
        if(nav.getCurrentPage().toLowerCase().indexOf("managemembers.jsp") >= 0)
            isMemberManager = true;
        
        LiteYukonUser liteUser = null;
        if (memberLogin == null ||
            (liteUser = LoginController.internalLogin(
                request,
                session,
                memberLogin.getUsername(),
                true)) == null)
        {
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "The member login is no longer valid" );
            String redirect = this.getRedirect(request);
            response.sendRedirect(redirect);
            return;
        }
        
        if(isMemberManager)
        {
            nav = new CtiNavObject();
            nav.setMemberECAdmin(isMemberManager);
            HttpSession sess = request.getSession(false);
            if(sess != null)
                sess.setAttribute(ServletUtils.NAVIGATE, nav);  
        }
        
        String redirect = this.authDao.getRolePropertyValue( liteUser, WebClientRole.HOME_URL );
        response.sendRedirect(redirect);
    }
    
}
