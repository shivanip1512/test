package com.cannontech.web.widget.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface WidgetController {

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
        throws Exception;

    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response)
        throws Exception;

}