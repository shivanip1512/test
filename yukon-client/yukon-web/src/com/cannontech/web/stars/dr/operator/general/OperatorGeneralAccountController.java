package com.cannontech.web.stars.dr.operator.general;

import java.beans.PropertyEditorSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.dr.general.service.AccountSearchResultHolder;
import com.cannontech.stars.dr.general.service.OperatorGeneralSearchService;
import com.cannontech.stars.dr.general.service.impl.AccountSearchResult;
import com.cannontech.stars.xml.serialize.StarsSubstations;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.option.producer.LeftSideContextualMenuOptionsProducer;
import com.cannontech.web.stars.dr.operator.OperatorActionsFactory;
import com.cannontech.web.stars.dr.operator.general.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.general.service.OperatorGeneralService;

@Controller
public class OperatorGeneralAccountController {

	private OperatorGeneralSearchService operatorGeneralSearchService;
	private OperatorGeneralService operatorGeneralService;
	private StarsDatabaseCache starsDatabaseCache;
	private CustomerAccountDao customerAccountDao;
	private AccountService accountService;
	private SiteInformationDao siteInformationDao;
	
	// SEARCH
	@RequestMapping(value = "/operator/general/account/search")
    public String search(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int searchByDefinitionId = ServletRequestUtils.getIntParameter(request, "searchByDefinitionId", OperatorAccountSearchBy.ACCOUNT_NUMBER.getDefinitionId());
		String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", "");
		int startIndex = ServletRequestUtils.getIntParameter(request, "startIndex", 0);
		int count = ServletRequestUtils.getIntParameter(request, "count", 25);
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		AccountSearchResultHolder accountSearchResultHolder = operatorGeneralSearchService.customerAccountSearch(searchByDefinitionId, searchValue, startIndex, count, energyCompany, userContext);
		
		// account edit
		if (accountSearchResultHolder.isSingleResult() && !accountSearchResultHolder.isHasError()) {
			
			AccountSearchResult accountSearchResult = accountSearchResultHolder.getAccountSearchResults().getResultList().get(0);
			
			modelMap.addAttribute("accountId", accountSearchResult.getAccountId());
			modelMap.addAttribute("energyCompanyId", accountSearchResult.getEnergyCompanyId());
			return "redirect:accountEdit";
			
		// account list
		} else {
			
			modelMap.addAttribute("accountSearchResultHolder", accountSearchResultHolder);
			return "operator/general/account/accountList.jsp";
		}
	}
	
	// ACCOUNT LIST
	@RequestMapping(value = "/operator/general/account/accountList")
    public String accountList(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		AccountSearchResultHolder accountSearchResultHolder = AccountSearchResultHolder.emptyAccountSearchResultHolder();
		modelMap.addAttribute("accountSearchResultHolder", accountSearchResultHolder);
		return "operator/general/account/accountList.jsp";
	}
	
	// ACCOUNT EDIT
	@RequestMapping(value = "/operator/general/account/accountEdit")
    public String accountEdit(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		// account
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		
		// accountGeneral
		AccountDto accountDto = accountService.getAccountDto(accountId, energyCompanyId);
		OperatorGeneralUiExtras operatorGeneralUiExtras = operatorGeneralService.getOperatorGeneralUiExtras(accountId, userContext);
		AccountGeneral accountGeneral = new AccountGeneral();
		accountGeneral.setAccountDto(accountDto);
		accountGeneral.setOperatorGeneralUiExtras(operatorGeneralUiExtras);
		modelMap.addAttribute("accountGeneral", accountGeneral);
		
		// substations
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		StarsSubstations substations = energyCompany.getStarsSubstations();
		modelMap.addAttribute("substations", substations);
		
		// basics
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		modelMap.addAttribute("accountNumber", accountDto.getAccountNumber());
		
		// leftSideContxtualMenuLinks
		LeftSideContextualMenuOptionsProducer leftSideContextualMenuOptionsProducer = OperatorActionsFactory.getLeftSideContxtualMenuLinks(accountId, energyCompanyId, "general", userContext);
		modelMap.addAttribute("leftSideContextualMenuOptionsProducer", leftSideContextualMenuOptionsProducer);
		
		return "operator/general/account/accountEdit.jsp";
	}
	
	// ACCOUNT UPDATE
	@RequestMapping(value = "/operator/general/account/accountUpdate")
    public String accountUpdate(@ModelAttribute("accountGeneral") AccountGeneral accountGeneral, 
								BindingResult bindingResult,
					    		int accountId,
					    		int energyCompanyId,
					    		ModelMap modelMap, 
					    		YukonUserContext userContext) {
		
		// account
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		String currentAccountNumber = customerAccount.getAccountNumber();
		modelMap.addAttribute("accountId", accountId);
		modelMap.addAttribute("energyCompanyId", energyCompanyId);
		
		// update accountDto
		UpdatableAccount updatableAccount = new UpdatableAccount();
		updatableAccount.setAccountNumber(currentAccountNumber);
		updatableAccount.setAccountDto(accountGeneral.getAccountDto());
		accountService.updateAccount(updatableAccount, energyCompany);
		
		// update operatorGeneralUiExtras
		operatorGeneralService.updateAccount(accountId, accountGeneral.getOperatorGeneralUiExtras());
		
		return "redirect:accountEdit";
	}
	
	// ACCOUNT DELETE
	@RequestMapping(value = "/operator/general/account/accountDelete")
    public String accountDelete(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		int accountId = ServletRequestUtils.getRequiredIntParameter(request, "accountId");
		int energyCompanyId = ServletRequestUtils.getRequiredIntParameter(request, "energyCompanyId");
		accountService.deleteAccount(accountId, energyCompanyId);
		
		return "redirect:/spring/stars/operator/home";
	}
	
	// INIT BINDER
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		
		binder.registerCustomEditor(Object.class, "accountDto.siteInfo.substationName", new PropertyEditorSupport() {
			
			@Override
            public void setAsText(String text) throws IllegalArgumentException {
                String substationName = siteInformationDao.getSubstationNameById(Integer.valueOf(text));
                setValue(substationName);
            }
            @Override
            public String getAsText() {
            	
            	String substationName = (String)getValue();
            	int substationId = siteInformationDao.getSubstationIdByName(substationName);
            	
            	return String.valueOf(substationId);
            }
		});
	}
	
	// ACCOUNT GENERAL WRAPPER
	public static class AccountGeneral {
		
		private AccountDto accountDto = new AccountDto();
		private OperatorGeneralUiExtras operatorGeneralUiExtras = new OperatorGeneralUiExtras();
		
		public AccountDto getAccountDto() {
			return accountDto;
		}
		public void setAccountDto(AccountDto accountDto) {
			this.accountDto = accountDto;
		}
		public OperatorGeneralUiExtras getOperatorGeneralUiExtras() {
			return operatorGeneralUiExtras;
		}
		public void setOperatorGeneralUiExtras(
				OperatorGeneralUiExtras operatorGeneralUiExtras) {
			this.operatorGeneralUiExtras = operatorGeneralUiExtras;
		}
	}
	
	
	@Autowired
	public void setOperatorGeneralSearchService(OperatorGeneralSearchService operatorGeneralSearchService) {
		this.operatorGeneralSearchService = operatorGeneralSearchService;
	}
	
	@Autowired
	public void setOperatorGeneralService(OperatorGeneralService operatorGeneralService) {
		this.operatorGeneralService = operatorGeneralService;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@Autowired
	public void setSiteInformationDao(SiteInformationDao siteInformationDao) {
		this.siteInformationDao = siteInformationDao;
	}
}
