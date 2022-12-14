package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

@Controller
public class ForgotPasswordController {
    
    @Autowired private GlobalSettingDao globalSettingDao;

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String forgotPassword(final HttpServletRequest request, final HttpServletResponse response) throws Exception {

        //Check global role property (we're not logged in at this point so user is null)
        globalSettingDao.verifySetting(GlobalSettingType.ENABLE_PASSWORD_RECOVERY);

        return "forgotPassword.jsp";
    }
}
