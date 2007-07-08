package com.cannontech.web.widget;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.web.widget.support.WidgetControllerBase;

public class ReadCurrentTimeWidget extends WidgetControllerBase {

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        Thread.sleep(750);
        ModelAndView mav = new ModelAndView();
        mav.addObject("time", new Date());
        return mav;
    }

}
