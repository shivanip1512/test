package com.cannontech.web.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;

public class InformationController implements Controller {
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Sets up the ModelAndView Object
        ModelAndView mav = new ModelAndView("info.jsp");

        mav.addObject("versionDetails", VersionTools.getYukonDetails());
        mav.addObject("systemInformation", CtiUtilities.getSystemInfoString());
        mav.addObject("buildInfo", VersionTools.getBuildInfo());
        
        return mav;
    }
    
}
