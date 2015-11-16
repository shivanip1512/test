package com.cannontech.web.stars.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;

public abstract class StarsAdminActionController extends AbstractBaseActionController {
    
    @Override
    public final String getRedirect(final HttpServletRequest request) throws Exception {
        String redirect = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT);
        
        redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
        return redirect;
    }
    
    @Override
    public final String getReferer(final HttpServletRequest request) throws Exception {
        final HttpSession session = request.getSession(false);
        final CtiNavObject nav = (CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE);
        
        String referer = (nav != null) ? 
                nav.getPreviousPage() : request.getHeader("referer");
        
        if (referer == null) {
            referer = AbstractBaseActionController.LOGIN_URL;
        }
        referer = ServletUtil.createSafeRedirectUrl(request, referer);
        return referer;
    }


    
}
