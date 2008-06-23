package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableScheduledOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;

@Controller
@RequestMapping("/consumer/general")
public class GeneralController extends AbstractConsumerController {
    private static final String viewName = "consumer/general.jsp";

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL)
    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            YukonUserContext yukonUserContext, ModelMap map) {
        
        List<DisplayableProgram> displayablePrograms = displayableProgramDao.getDisplayablePrograms(customerAccount, yukonUserContext);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        boolean isNotEnrolled = displayablePrograms.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);

        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show
        
        DisplayableScheduledOptOut displayableOptOut = 
            displayableScheduledOptOutDao.getLastDisplayableScheduledOptOut(customerAccount,
                                                                   yukonUserContext);
        map.addAttribute("displayableOptOut", displayableOptOut);
        
        return viewName;
    }
    
}
