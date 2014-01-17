package com.cannontech.web.stars.dr.consumer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL)
@Controller
@RequestMapping("/consumer/contactus")
public class ContactUsController extends AbstractConsumerController {
    
    @RequestMapping(value="view", method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount) {
        return "consumer/contactUs.jsp";
    }
    
}
