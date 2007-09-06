package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.service.SwitchContextService;

public class StarsActionController implements Controller,BeanFactoryAware {
    private BeanFactory beanFactory;
    private String prefix;
    private String defaultBeanName;
    private SwitchContextService switchContextService;
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final boolean isMultiPart = ServletUtils.isMultiPartRequest(request);

        final String action = (isMultiPart) ?
                ServletUtils.getFormField(ServletUtils.getItemList(request), "action") : ServletRequestUtils.getStringParameter(request, "action");
        
        final String beanName = (action != null) ? prefix + action : prefix + defaultBeanName;

        if (prefix.equalsIgnoreCase("workorder")) {
            Integer memberID = ServletRequestUtils.getIntParameter(request, "SwitchContext");
            if (memberID != null) {
                HttpSession session = request.getSession(false);
                try {
                    final StarsYukonUser user = ServletUtils.getStarsYukonUser(request);
                    switchContextService.switchContext(user, request, session, memberID);
                } catch (WebClientException e) {
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                    String referer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER);
                    if (referer == null) referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
                    response.sendRedirect(referer);
                    return null;
                }
            }
        }
        
        Controller actionController = (Controller) beanFactory.getBean(beanName, Controller.class);
        actionController.handleRequest(request, response);
        return null;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void setDefaultBeanName(String defaultBeanName) {
        this.defaultBeanName = defaultBeanName;
    }

    public void setSwitchContextService(SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
}
