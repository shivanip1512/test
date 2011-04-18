package com.cannontech.web.stars.dr.operator;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerResidenceDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerResidence;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.CustomerResidenceValidator;

@Controller
@RequestMapping(value = "/operator/residence/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ACCOUNT_RESIDENCE)
public class OperatorResidenceController {

	private CustomerAccountDao customerAccountDao;
	private CustomerResidenceDao customerResidenceDao;
	private CustomerResidenceValidator customerResidenceValidator;
	private RolePropertyDao rolePropertyDao;
	
	// RESIDENCE VIEW PAGE
	@RequestMapping
	public String view(int accountId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
	    model.addAttribute("mode", PageEditMode.VIEW);
	    
	    setupResidenceModel(model, fragment);
	    
	    return "operator/residence/residenceEdit.jsp";
	}
	
	// RESIDENCE EDIT PAGE
	@RequestMapping
    public String edit(int accountId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
	    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
	    model.addAttribute("mode", PageEditMode.EDIT);
	    
	    setupResidenceModel(model, fragment);
	    
		return "operator/residence/residenceEdit.jsp";
	}
	
	private void setupResidenceModel(ModelMap model, AccountInfoFragment fragment) {
	    model.addAttribute("energyCompanyId", fragment.getEnergyCompanyId());
        
        // CustomerResidence
        CustomerAccount customerAccount = customerAccountDao.getById(fragment.getAccountId());
        CustomerResidence customerResidence = customerResidenceDao.findByAccountSiteId(customerAccount.getAccountSiteId());
        if (customerResidence == null) {
            customerResidence = new CustomerResidence();
        }
        model.addAttribute("customerResidence", customerResidence);
        
        AccountInfoFragmentHelper.setupModelMapBasics(fragment, model);
	}
	
	// RESIDENCE UPDATE
	@RequestMapping(method=RequestMethod.POST)
    public String residenceUpdate(@ModelAttribute("customerResidence") CustomerResidence customerResidence, 
    								BindingResult bindingResult,
						    		int accountId,
						    		ModelMap modelMap, 
						    		YukonUserContext userContext,
						    		FlashScope flashScope,
						    		AccountInfoFragment accountInfoFragment) {
		
		rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		
		// validate/insert/update
		MessageSourceResolvable okMessage = null;
		customerResidenceValidator.validate(customerResidence, bindingResult);
		if (!bindingResult.hasErrors()) {
			
			if (customerResidence.getAccountSiteId() <= 0) {
				
				CustomerAccount customerAccount = customerAccountDao.getById(accountId);
				customerResidence.setAccountSiteId(customerAccount.getAccountSiteId());
				customerResidenceDao.insert(customerResidence);
				okMessage = new YukonMessageSourceResolvable("yukon.web.modules.operator.residence.residenceCreated");
				
			} else {
				customerResidenceDao.update(customerResidence);
				okMessage = new YukonMessageSourceResolvable("yukon.web.modules.operator.residence.residenceUpdated");
			}
		}
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		if (bindingResult.hasErrors()) {
			List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
			flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
			modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
			modelMap.addAttribute("mode", PageEditMode.EDIT);
			return "operator/residence/residenceEdit.jsp";
		} 
		
		flashScope.setConfirm(Collections.singletonList(okMessage));
		modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
		return "redirect:view";
	}
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setCustomerResidenceDao(CustomerResidenceDao customerResidenceDao) {
		this.customerResidenceDao = customerResidenceDao;
	}
	
	@Autowired
	public void setCustomerResidenceValidator(CustomerResidenceValidator customerResidenceValidator) {
		this.customerResidenceValidator = customerResidenceValidator;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
}