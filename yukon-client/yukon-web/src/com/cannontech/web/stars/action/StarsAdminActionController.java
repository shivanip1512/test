package com.cannontech.web.stars.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.navigation.CtiNavObject;

public abstract class StarsAdminActionController extends AbstractBaseActionController {
    
    @Override
    public final String getRedirect(final HttpServletRequest request) throws Exception {
        String redirect = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT);
        
        if (redirect == null) {
            redirect = request.getContextPath() + "/operator/Admin/AdminTest.jsp";
        }
        
        return redirect;
    }
    
    @Override
    public final String getReferer(final HttpServletRequest request) throws Exception {
        final HttpSession session = request.getSession(false);
        final CtiNavObject nav = (CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE);
        
        String referer = (nav != null) ? 
                nav.getPreviousPage() : request.getHeader("referer");
        
        if (referer == null) {
            referer = request.getContextPath() + AbstractBaseActionController.LOGIN_URL;
        }
        
        return referer;
    }


    
}
