package com.cannontech.web.stars.dr.consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.contactus.ContactUs;
import com.cannontech.web.stars.dr.consumer.contactus.ContactUsDao;

@Controller
@RequestMapping("/consumer/contactus")
public class ContactUsController extends AbstractConsumerController {
    private ContactUsDao contactUsDao;
    
    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_QUESTIONS_UTIL)
    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        
        ContactUs contactUs = contactUsDao.getContactUsByCustomerAccount(customerAccount);
        map.addAttribute("contactUs", contactUs);
        
        return "consumer/contactUs.jsp";
    }
    
    @Autowired
    public void setContactUsDao(ContactUsDao contactUsDao) {
        this.contactUsDao = contactUsDao;
    }
    
}
