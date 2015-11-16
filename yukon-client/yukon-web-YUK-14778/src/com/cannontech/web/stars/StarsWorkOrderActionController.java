package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.service.SwitchContextService;

public class StarsWorkOrderActionController extends StarsActionController {
    private SwitchContextService switchContextService;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        final Integer memberID = ServletRequestUtils.getIntParameter(request, "SwitchContext");
        
        if (memberID != null) {
            HttpSession session = request.getSession(false);
            try {
                final StarsYukonUser user = ServletUtils.getStarsYukonUser(request);
                switchContextService.switchContext(user, request, session, memberID);
            } catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                String referer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER);
                if (referer == null) referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
                referer = ServletUtil.createSafeRedirectUrl(request, referer);
                response.sendRedirect(referer);
                return null;
            }
        }
        
        return super.handleRequest(request, response);
    }
    
    public void setSwitchContextService(SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
}
