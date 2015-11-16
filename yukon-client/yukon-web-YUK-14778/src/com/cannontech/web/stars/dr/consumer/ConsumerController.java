package com.cannontech.web.stars.dr.consumer;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.core.service.AccountCheckerService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.general.dao.FaqDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.login.ChangeLoginMessage;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/consumer/*")
public class ConsumerController extends AbstractConsumerController {

    @Autowired private FaqDao faqDao;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private AccountCheckerService accountCheckerService;
    @Autowired private GraphDao graphDao;

    @CheckRoleProperty({YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME,
                        YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD})
    @RequestMapping(value="changelogin", method = RequestMethod.GET)
    public String changelogin(HttpServletRequest request, ModelMap map) throws Exception {
        
        String loginChangeMsgParam = ServletRequestUtils.getStringParameter(request, "loginChangeMsg");
        String loginChangeSuccessParam = ServletRequestUtils.getStringParameter(request, "success");
        if (loginChangeMsgParam != null) {
            ChangeLoginMessage loginMsg = ChangeLoginMessage.valueOf(loginChangeMsgParam);
            map.addAttribute("loginChangeMsg", loginMsg);
        }
        if (loginChangeSuccessParam != null) {
            boolean success = Boolean.parseBoolean(loginChangeSuccessParam);
            map.addAttribute("success", success);
        }        
        return "consumer/consumerChangeLogin.jsp";
    }

    @CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL)
    @RequestMapping(value="contactus", method=GET)
    public String contactus() {
        return "consumer/contactUs.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ)
    @RequestMapping(value="faq", method=GET)
    public String faq(YukonUserContext yukonUserContext, ModelMap map) {
        Map<String, Map<String, String>> questions = faqDao.getQuestionMap(yukonUserContext);
        map.addAttribute("questions", questions);
        return "consumer/faq.jsp";
    }
}
