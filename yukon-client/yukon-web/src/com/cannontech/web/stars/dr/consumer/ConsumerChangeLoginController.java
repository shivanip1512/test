package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.web.login.ChangeLoginController;
import com.cannontech.web.login.ChangeLoginError;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/consumer/changelogin")
public class ConsumerChangeLoginController extends AbstractConsumerController {

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_ADMIN_CHANGE_LOGIN)
    @RequestMapping(method = RequestMethod.GET)
    public String view(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws Exception {
        
        String loginErrorParam = ServletRequestUtils.getStringParameter(request, ChangeLoginController.LOGIN_ERROR_PARAM);
        if (loginErrorParam != null) {
            ChangeLoginError loginError = ChangeLoginError.valueOf(loginErrorParam);
            map.addAttribute(ChangeLoginController.LOGIN_ERROR_PARAM, loginError);
        }
        
        return "consumer/consumerChangeLogin.jsp";
    }
    
}
