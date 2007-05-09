package com.cannontech.web.widget.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;

public class WidgetExceptionHandler implements HandlerExceptionResolver {
    private Logger log = YukonLogManager.getLogger(WidgetExceptionHandler.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        log.warn("Error processing this widget", ex);
        ModelAndView mav = new ModelAndView("widgetError.jsp");
        //TODO fix after ServletUtil is changed
        mav.addObject("stackTrace", "keep your pants on, this is coming...");
        mav.addObject("errorMessage", ex.getMessage());
        return mav;
    }

}
