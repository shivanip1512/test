package com.cannontech.web.widget.support;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FriendlyExceptionResolver;

public class WidgetExceptionHandler implements HandlerExceptionResolver {
    private Logger log = YukonLogManager.getLogger(WidgetExceptionHandler.class);
    private FriendlyExceptionResolver friendlyExceptionResolver = null;

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        log.warn("Error processing this widget", ex);
        ModelAndView mav = new ModelAndView("widgetError.jsp");
        
        String friendlyExceptionMessage = friendlyExceptionResolver.getExceptionMessage(ex, Locale.US);
        mav.addObject("errorMessage", friendlyExceptionMessage);
        return mav;
    }
    
    @Required
    public void setFriendlyExceptionResolver(
            FriendlyExceptionResolver friendlyExceptionResolver) {
        this.friendlyExceptionResolver = friendlyExceptionResolver;
    }

}
