package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

@Controller
public class ForgotPasswordController {
    
    @Autowired private YukonSettingsDao yukonSettingsDao;

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPassword(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    	
    	//Check global role property (we're not logged in at this point so user is null)
        yukonSettingsDao.verifySetting(YukonSetting.ENABLE_PASSWORD_RECOVERY);
    	
    	final ModelAndView mav = new ModelAndView();
    	mav.setViewName("forgotPassword.jsp");
    	return mav;
    }
}
