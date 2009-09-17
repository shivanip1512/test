package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.web.widget.support.WidgetControllerBase;

/**
 * Widget used to display the system actions available.
 */
public class SystemActionsMenuWidget extends WidgetControllerBase {

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("systemActionsMenuWidget/render.jsp");

        return mav;
    }

}
