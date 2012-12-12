package com.cannontech.web.stars.dr.operator.enrollment;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.Pair;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.enrollment.service.AlternateEnrollmentService;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/operator/ae/*")
@CheckRoleProperty(YukonRoleProperty.ALTERNATE_PROGRAM_ENROLLMENT)
public class OperatorAlternateEnrollmentController {
    
    @Autowired private AlternateEnrollmentService aeService;
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        /* inventory to: list of normal programs, list of alternate programs */
        Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> available = Maps.newHashMap();
        /* inventory to: list of alternate programs, list of normal programs */
        Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> active = Maps.newHashMap();
        
        aeService.buildEnrollmentMaps(accountInfoFragment.getAccountId(), available, active);
        
        if (!available.isEmpty()) model.addAttribute("available", available);
        if (!active.isEmpty()) model.addAttribute("active", active);
        
        return "operator/enrollment/view.jsp";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String enroll(ModelMap model, 
    		final Integer[] alternate, 
    		final Integer[] normal, 
    		final AccountInfoFragment fragment, 
    		final FlashScope flash, 
    		final YukonUserContext context) {
    	
    	aeService.doEnrollment(alternate, normal, fragment.getAccountId(), context);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.alternateEnrollment.alternate.success"));
    	AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
    	return "redirect:view";
    }
    
}