package com.cannontech.web.stars.dr.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.model.Appliance;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.consumer.display.DisplayableOptOut;
import com.cannontech.web.stars.dr.consumer.display.DisplayableProgram;

@Controller
@RequestMapping("/consumer/general")
public class GeneralController extends AbstractConsumerController {
    private static final String viewName = "consumer/general.jsp";

    @RequestMapping(method = RequestMethod.GET)
    public String view(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        CustomerAccount customerAccount = getCustomerAccount(request);
        final int customerAccountId = customerAccount.getAccountId();
        
        List<Appliance> applianceList = applianceDao.getByAccountId(customerAccountId);
        List<Program> programs = programDao.getByAppliances(applianceList);

        boolean isNotEnrolled = programs.size() == 0;
        map.addAttribute("isNotEnrolled", isNotEnrolled);
        
        if (isNotEnrolled) return viewName; // if there are no programs enrolled there is nothing more to show
        
        List<DisplayableProgram> displayablePrograms = displayableProgramDao.getDisplayablePrograms(customerAccount, yukonUserContext);
        map.addAttribute("displayablePrograms", displayablePrograms);
        
        DisplayableOptOut displayableOptOut = displayableProgramDao.getDisplayableOptOut(customerAccount, yukonUserContext);
        map.addAttribute("displayableOptOut", displayableOptOut);
        
        return viewName;
    }
    
}
