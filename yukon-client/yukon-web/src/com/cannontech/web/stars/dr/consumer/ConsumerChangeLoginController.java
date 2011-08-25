package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.login.ChangeLoginMessage;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty({YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD})
@Controller
@RequestMapping("/consumer/changelogin")
public class ConsumerChangeLoginController extends AbstractConsumerController {
    private static final String LOGIN_CHANGE_MESSAGE_PARAM = "loginChangeMsg";
    private static final String LOGIN_CHANGE_SUCCESS_PARAM = "success";

    @RequestMapping(method = RequestMethod.GET)
    public String view(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws Exception {
        
        String loginChangeMsgParam = ServletRequestUtils.getStringParameter(request, LOGIN_CHANGE_MESSAGE_PARAM);
        String loginChangeSuccessParam = ServletRequestUtils.getStringParameter(request, LOGIN_CHANGE_SUCCESS_PARAM);
        if (loginChangeMsgParam != null) {
        	ChangeLoginMessage loginMsg = ChangeLoginMessage.valueOf(loginChangeMsgParam);
            map.addAttribute(LOGIN_CHANGE_MESSAGE_PARAM, loginMsg);
        }
        if (loginChangeSuccessParam != null) {
        	boolean success = Boolean.parseBoolean(loginChangeSuccessParam);
        	map.addAttribute(LOGIN_CHANGE_SUCCESS_PARAM, success);
        }        
        return "consumer/consumerChangeLogin.jsp";
    }
}
