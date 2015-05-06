package com.cannontech.web.stars.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.navigation.CtiNavObject;

public abstract class StarsWorkorderActionController extends AbstractBaseActionController {

    @Override
    public String getRedirect(HttpServletRequest request) throws ServletRequestBindingException {
        String confirmOnMessage = ServletRequestUtils.getStringParameter(request, ServletUtils.CONFIRM_ON_MESSAGE_PAGE);
        String redirect = buildRedirect(request);
        
        if (confirmOnMessage != null) {
            HttpSession session = request.getSession(false);
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, redirect);
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, buildReferer(request));
            redirect = request.getContextPath() + "/operator/Admin/Message.jsp";
        }

        return redirect;
    }
    
    @Override
    public String getReferer(HttpServletRequest request) throws ServletRequestBindingException  {
        String confirmOnMessage = ServletRequestUtils.getStringParameter(request, ServletUtils.CONFIRM_ON_MESSAGE_PAGE);
        String referer = buildReferer(request);
        
        if (confirmOnMessage != null) {
            HttpSession session = request.getSession(false);
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, buildRedirect(request));
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, referer);
            referer = request.getContextPath() + "/operator/Admin/Message.jsp";
        }
        return referer;
    }
    
    private String buildRedirect(final HttpServletRequest request) throws ServletRequestBindingException {
        String redirect = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT);
        
        if (redirect == null) {
            redirect = getReferer(request);
        }
        
        return redirect;
    }
    
    private String buildReferer(final HttpServletRequest request) throws ServletRequestBindingException {
        String referer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER);
        
        if(referer == null) {
            HttpSession session = request.getSession(false);
            referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
        }
        
        return referer;
    }
    
}
