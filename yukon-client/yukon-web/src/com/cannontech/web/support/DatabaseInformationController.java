package com.cannontech.web.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.database.PoolManager;

public class DatabaseInformationController implements Controller {
    private PoolManager poolManager;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Sets up the ModelAndView Object
        ModelAndView mav = new ModelAndView("database/info.jsp");

        mav.addObject("dbUser", poolManager.getPrimaryUser());
        mav.addObject("dbUrl", poolManager.getPrimaryUrl());
        
        return mav;
    }
    
    @Autowired
    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }
}
