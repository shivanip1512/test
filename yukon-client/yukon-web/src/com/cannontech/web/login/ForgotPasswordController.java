package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;

@Controller
public class ForgotPasswordController {
    
	private RolePropertyDao rolePropertyDao;
	
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPassword(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    	
    	//Check global role property (we're not logged in at this point so user is null)
    	rolePropertyDao.verifyProperty(YukonRoleProperty.ENABLE_PASSWORD_RECOVERY, null);
    	
    	final ModelAndView mav = new ModelAndView();
    	mav.setViewName("forgotPassword.jsp");
    	return mav;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
}
