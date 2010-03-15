package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.event.EventAccount;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.validator.AccountGeneralValidator;

@Controller
@RequestMapping(value = "/operator/account/*")
public class OperatorAccountController {

	private OperatorGeneralSearchService operatorGeneralSearchService;
	private OperatorAccountService operatorAccountService;
	private StarsDatabaseCache starsDatabaseCache;
	private CustomerAccountDao customerAccountDao;
	private AccountService accountService;
	private AccountGeneralValidator accountGeneralValidator;
	
	// SEARCH
	@RequestMapping
    public String search(HttpServletRequest request,
    					Integer itemsPerPage, 
    					Integer page,
    					ModelMap modelMap, 
    					YukonUserContext userContext,
    					FlashScope flashScope) throws ServletRequestBindingException {
		
		OperatorAccountSearchBy searchBy = OperatorAccountSearchBy.valueOf(ServletRequestUtils.getStringParameter(request, "searchBy", OperatorAccountSearchBy.ACCOUNT_NUMBER.name()));
		String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", "");
		
		// paging
		if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 25;
        }
        int startIndex = (page - 1) * itemsPerPage;
		
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		AccountSearchResultHolder accountSearchResultHolder = operatorGeneralSearchService.customerAccountSearch(searchBy, searchValue, startIndex, itemsPerPage, energyCompany, userContext);
		
		// account edit
		if (accountSearchResultHolder.isSingleResult() && !accountSearchResultHolder.isHasWarning()) {
			
			AccountSearchResult accountSearchResult = accountSearchResultHolder.getAccountSearchResults().getResultList().get(0);
			
			modelMap.addAttribute("accountId", accountSearchResult.getAccountId());
			modelMap.addAttribute("energyCompanyId", accountSearchResult.getEnergyCompanyId());
			
			return "redirect:accountEdit";
			
		// account list
		} else {
			
			if (accountSearchResultHolder.isHasWarning()) {
				flashScope.setWarning(Collections.singletonList(accountSearchResultHolder.getWarning()));
			}
			Object[] searchResultTitleArguments = new Object[]{new YukonMessageSourceResolvable(searchBy.getFormatKey()), searchValue};
			modelMap.addAttribute("searchResultTitleArguments", searchResultTitleArguments);
			modelMap.addAttribute("accountSearchResultHolder", accountSearchResultHolder);
			return "operator/account/accountList.jsp";
		}
	}
	
	// ACCOUNT LIST
	@RequestMapping
    public String accountList(HttpServletRequest request, ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
		
		AccountSearchResultHolder accountSearchResultHolder = AccountSearchResultHolder.emptyAccountSearchResultHolder();
		modelMap.addAttribute("accountSearchResultHolder", accountSearchResultHolder);
		return "operator/account/accountList.jsp";
	}
	
	// ACCOUNT EDIT
	@RequestMapping
    public String accountEdit(int accountId,
    						  int energyCompanyId, 
    						  ModelMap modelMap, 
    						  YukonUserContext userContext,
    						  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
		
		// accountGeneral
		AccountDto accountDto = accountService.getAccountDto(accountId, energyCompanyId, userContext);
		OperatorGeneralUiExtras operatorGeneralUiExtras = operatorAccountService.getOperatorGeneralUiExtras(accountId, userContext);
		AccountGeneral accountGeneral = new AccountGeneral();
		accountGeneral.setAccountDto(accountDto);
		accountGeneral.setOperatorGeneralUiExtras(operatorGeneralUiExtras);
		modelMap.addAttribute("accountGeneral", accountGeneral);
		
		setupAccountEditModelMap(accountInfoFragment, modelMap, userContext);
		return "operator/account/accountEdit.jsp";
	}
	
	// ACCOUNT UPDATE
	@RequestMapping
    public String accountUpdate(@ModelAttribute("accountGeneral") AccountGeneral accountGeneral, 
								BindingResult bindingResult,
								int accountId,
	    						int energyCompanyId, 
					    		ModelMap modelMap, 
					    		YukonUserContext userContext,
					    		FlashScope flashScope,
					    		AccountInfoFragment accountInfoFragment) {
		
		// account
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		String currentAccountNumber = customerAccount.getAccountNumber();
		
		// UpdatableAccount
		UpdatableAccount updatableAccount = new UpdatableAccount();
		updatableAccount.setAccountNumber(currentAccountNumber);
		updatableAccount.setAccountDto(accountGeneral.getAccountDto());
		
		// validate/update
		try {
			
			accountGeneralValidator.validate(accountGeneral, bindingResult);
			
			if (!bindingResult.hasErrors()) {
				accountService.updateAccount(updatableAccount, energyCompany);
				operatorAccountService.updateAccount(accountId, accountGeneral.getOperatorGeneralUiExtras());
			}
			
		} catch (AccountNumberUnavailableException e) {
			
			bindingResult.rejectValue("accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberUnavailable");
		
		} finally {
			
			setupAccountEditModelMap(accountInfoFragment, modelMap, userContext);
			if (bindingResult.hasErrors()) {
				flashScope.setBindingResult(bindingResult);
				return "operator/account/accountEdit.jsp";
			} 
		}
		
		flashScope.setConfirm(Collections.singletonList(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountEdit.accountUpdated")));
		return "redirect:accountEdit";
	}
	
	
	
	// ACCOUNT DELETE
	@RequestMapping
    public String accountDelete(int accountId,
    						  	int energyCompanyId, 
    							ModelMap modelMap, 
    							YukonUserContext userContext,
    							FlashScope flashScope) throws ServletRequestBindingException {
		
		accountService.deleteAccount(accountId, energyCompanyId);
		
		flashScope.setConfirm(Collections.singletonList(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountEdit.accountDeleted")));
		return "redirect:/spring/stars/operator/home";
	}
	
	// ACCOUNT LOG
	@RequestMapping
    public String accountLog(int accountId,
		  					 int energyCompanyId, 
    						 ModelMap modelMap, 
    						 YukonUserContext userContext,
    						 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        ArrayList<EventAccount> accountEvents = EventAccount.retrieveEventAccounts(accountId);
        modelMap.addAttribute("accountEvents",accountEvents);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/account/accountLog.jsp";
    }
	
	// MISC MODEL ITEMS FOR ACCOUNT EDIT
	private void setupAccountEditModelMap(AccountInfoFragment accountInfoFragment, ModelMap modelMap, YukonUserContext userContext) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// substations
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(accountInfoFragment.getEnergyCompanyId());
		StarsSubstations substations = energyCompany.getStarsSubstations();
		modelMap.addAttribute("substations", substations);
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
	public void setOperatorAccountService(OperatorAccountService operatorAccountService) {
		this.operatorAccountService = operatorAccountService;
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
	public void setAccountGeneralValidator(AccountGeneralValidator accountGeneralValidator) {
		this.accountGeneralValidator = accountGeneralValidator;
	}
}
