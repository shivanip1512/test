package com.cannontech.web.stars.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;

public interface SwitchContextService {

    public void switchContext(StarsYukonUser user, HttpServletRequest request, HttpSession session, int memberID) 
        throws WebClientException;
    
}
