package com.cannontech.web.stars.dr.operator;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerResidenceDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerResidence;
import com.cannontech.user.YukonUserContext;
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
	
	// RESIDENCE
	@RequestMapping
    public String residenceEdit(int accountId,
    							ModelMap modelMap, 
    							YukonUserContext userContext,
    							AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        // CustomerResidence
        CustomerResidence customerResidence = customerResidenceDao.findByAccountSiteId(accountId);
        if (customerResidence == null) {
        	customerResidence = new CustomerResidence();
        }
        modelMap.addAttribute("customerResidence", customerResidence);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		return "operator/residence/residenceEdit.jsp";
	}
	
	// RESIDENCE UPDATE
	@RequestMapping
    public String residenceUpdate(@ModelAttribute("customerResidence") CustomerResidence customerResidence, 
    								BindingResult bindingResult,
						    		int accountId,
						    		ModelMap modelMap, 
						    		YukonUserContext userContext,
						    		FlashScope flashScope,
						    		AccountInfoFragment accountInfoFragment) {
		
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
			return "operator/residence/residenceEdit.jsp";
		} 
		
		flashScope.setConfirm(Collections.singletonList(okMessage));
		return "redirect:residenceEdit";
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
}
