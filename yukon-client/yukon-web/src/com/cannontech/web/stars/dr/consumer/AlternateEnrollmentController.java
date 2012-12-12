package com.cannontech.web.stars.dr.consumer;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.Pair;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.service.AlternateEnrollmentService;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/consumer/ae/*")
@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT)
public class AlternateEnrollmentController extends AbstractConsumerController {
	
	@Autowired private AlternateEnrollmentService aeService;
	@Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private YukonEnergyCompanyService yecService;
    @Autowired private EnergyCompanyRolePropertyDao ecRolePropertyDao;
	
    private static final String VIEW = "consumer/alternateEnrollment.jsp";
       
    @RequestMapping
    public String view(@ModelAttribute CustomerAccount customerAccount, ModelMap model, YukonUserContext context) {
    	
        YukonEnergyCompany yec = yecService.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        ecRolePropertyDao.verifyProperty(YukonRoleProperty.ALTERNATE_PROGRAM_ENROLLMENT, yec);
        
        /* inventory to: set of normal programs, set of alternate programs */
        Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> available = Maps.newHashMap();
        /* inventory to: set of alternate programs, set of normal programs */
        Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> active = Maps.newHashMap();
        
        aeService.buildEnrollmentMaps(customerAccount.getAccountId(), available, active);
        
        if (!available.isEmpty()) model.addAttribute("available", available);
        if (!active.isEmpty()) model.addAttribute("active", active);
        
        return VIEW;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String enroll(@ModelAttribute CustomerAccount customerAccount, 
    		ModelMap model, 
    		final Integer[] alternate, 
    		final Integer[] normal, 
    		final FlashScope flash, 
    		final YukonUserContext context) {
    	
    	YukonEnergyCompany yec = yecService.getEnergyCompanyByAccountId(customerAccount.getAccountId());
        ecRolePropertyDao.verifyProperty(YukonRoleProperty.ALTERNATE_PROGRAM_ENROLLMENT, yec);
    	
    	aeService.doEnrollment(alternate, normal, customerAccount.getAccountId(), context);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.consumer.alternateEnrollment.alternate.success"));
    	return "redirect:view";
    }
        
}