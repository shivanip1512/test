package com.cannontech.web.stars.dr.operator.general;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerResidenceDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerResidence;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.producer.LeftSideContextualMenuOptionsProducer;
import com.cannontech.web.stars.dr.operator.OperatorActionsFactory;

@Controller
@SessionAttributes("customerResidence")
public class OperatorGeneralResidenceController {

	private CustomerAccountDao customerAccountDao;
	private CustomerResidenceDao customerResidenceDao;
	
	// RESIDENCE
	@RequestMapping(value = "/operator/general/residence/residenceEdit")
    public String residenceEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		// account
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		modelMap.addAttribute("accountNumber", customerAccount.getAccountNumber());
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		// leftSideContextualMenuOptionsProducer
		LeftSideContextualMenuOptionsProducer leftSideContextualMenuOptionsProducer = OperatorActionsFactory.getLeftSideContxtualMenuLinks(accountId, energyCompanyId, "residence", userContext);
		modelMap.addAttribute("leftSideContextualMenuOptionsProducer", leftSideContextualMenuOptionsProducer);
        
        // current residence
        CustomerResidence customerResidence = customerResidenceDao.findByAccountSiteId(customerAccount.getAccountId());
        modelMap.addAttribute("customerResidence", customerResidence);
        
		return "operator/general/residence/residenceEdit.jsp";
	}
	
	// RESIDENCE UPDATE
	@RequestMapping(value = "/operator/general/residence/residenceUpdate")
    public String residenceUpdate(@ModelAttribute("customerResidence") CustomerResidence customerResidence, 
    								BindingResult bindingResult,
						    		int accountId,
						    		int energyCompanyId,
						    		ModelMap modelMap, 
						    		YukonUserContext userContext) {
		
		// account
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		customerResidenceDao.update(customerResidence);
		
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
}
