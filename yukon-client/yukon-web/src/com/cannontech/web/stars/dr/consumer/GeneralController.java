package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableOptOut;
import com.cannontech.web.stars.dr.consumer.displayable.model.DisplayableProgram;

@Controller
@RequestMapping("/consumer/general")
public class GeneralController extends AbstractConsumerController {
    private static final String viewName = "consumer/general.jsp";

    @CheckRole(ResidentialCustomerRole.ROLEID)
    @CheckRoleProperty(ResidentialCustomerRole.CONSUMER_INFO_ACCOUNT_GENERAL)
    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute("customerAccount") CustomerAccount customerAccount,
            HttpServletRequest request, HttpServletResponse response, ModelMap map) {

        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        
        List<DisplayableProgram> displayablePrograms = displayableProgramDao.getDisplayablePrograms(customerAccount, yukonUserContext);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        boolean isNotEnrolled = displayablePrograms.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);

        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show
        
        DisplayableOptOut displayableOptOut = displayableOptOutDao.getDisplayableOptOut(customerAccount, yukonUserContext);
        map.addAttribute("displayableOptOut", displayableOptOut);
        
        return viewName;
    }
    
}
