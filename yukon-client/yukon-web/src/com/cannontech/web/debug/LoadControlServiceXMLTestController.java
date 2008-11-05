package com.cannontech.web.debug;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.util.ServletUtil;

public class LoadControlServiceXMLTestController extends MultiActionController {
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return returnMav(request, new ArrayList<String>(0));
    }
    
    // HELPERS
    private ModelAndView returnMav(HttpServletRequest request, List<String> results) {
        
        ModelAndView mav = new ModelAndView("loadControlService/xml/home.jsp");
     
        // re-populate fields
        mav.addAllObjects(ServletUtil.getParameterMap(request));
        mav.addObject("results", results);
        
        return mav;
    }
    
    
    
}
