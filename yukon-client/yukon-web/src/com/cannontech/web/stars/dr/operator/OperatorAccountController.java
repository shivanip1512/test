package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.Substation;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.SubstationDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.stars.event.EventAccount;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
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
	private RolePropertyDao rolePropertyDao;
	private ECMappingDao ecMappingDao;
	private SubstationDao substationDao;
	
	// SEARCH
	@RequestMapping
    public String search(HttpServletRequest request,
    					Integer itemsPerPage, 
    					Integer page,
    					ModelMap modelMap, 
    					YukonUserContext userContext,
    					FlashScope flashScope) throws ServletRequestBindingException {
		
		String searchByStr = ServletRequestUtils.getStringParameter(request, "searchBy", null);
		
		// no search
		if (StringUtils.isBlank(searchByStr)) {
			modelMap.addAttribute("accountSearchResultHolder", AccountSearchResultHolder.emptyAccountSearchResultHolder());
			modelMap.addAttribute("operatorAccountSearchBys", OperatorAccountSearchBy.values());
			return "operator/account/accountList.jsp";
		}
		
		// searchBy, searchValue
		OperatorAccountSearchBy searchBy = OperatorAccountSearchBy.valueOf(searchByStr);
		String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", null);
		
		// paging
		if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 25;
        }
        int startIndex = (page - 1) * itemsPerPage;
		
        // it is important for searching to use the energyCompany of the user
		LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
		AccountSearchResultHolder accountSearchResultHolder = operatorGeneralSearchService.customerAccountSearch(searchBy, searchValue, startIndex, itemsPerPage, energyCompany, userContext);
		
		// account edit
		if (accountSearchResultHolder.isSingleResult() && !accountSearchResultHolder.isHasWarning()) {
			
			AccountSearchResult accountSearchResult = accountSearchResultHolder.getAccountSearchResults().getResultList().get(0);
			
			modelMap.addAttribute("accountId", accountSearchResult.getAccountId());
			
			return "redirect:accountEdit";
			
		// account list
		} else {
			
			if (accountSearchResultHolder.isHasWarning()) {
				flashScope.setWarning(Collections.singletonList(accountSearchResultHolder.getWarning()));
			}
			Object[] searchResultTitleArguments = new Object[]{new YukonMessageSourceResolvable(searchBy.getFormatKey()), searchValue};
			modelMap.addAttribute("searchResultTitleArguments", searchResultTitleArguments);
			modelMap.addAttribute("accountSearchResultHolder", accountSearchResultHolder);
			modelMap.addAttribute("operatorAccountSearchBys", OperatorAccountSearchBy.values());
			return "operator/account/accountList.jsp";
		}
	}
	
	// ACCOUNT EDIT
	@RequestMapping
    public String accountEdit(int accountId,
    						  ModelMap modelMap, 
    						  YukonUserContext userContext,
    						  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
		
		// accountGeneral
		int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
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
					    		ModelMap modelMap, 
					    		YukonUserContext userContext,
					    		FlashScope flashScope,
					    		AccountInfoFragment accountInfoFragment) {
		
		// account
		int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
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
				
				List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
				flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
				
				return "operator/account/accountEdit.jsp";
			} 
		}
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountEdit.accountUpdated"));
		return "redirect:accountEdit";
	}
	
	
	
	// ACCOUNT DELETE
	@RequestMapping
    public String accountDelete(int accountId,
    							ModelMap modelMap, 
    							YukonUserContext userContext,
    							FlashScope flashScope) throws ServletRequestBindingException {
		
		int energyCompanyId = ecMappingDao.getEnergyCompanyIdForAccountId(accountId);
		
		accountService.deleteAccount(accountId, energyCompanyId);
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountEdit.accountDeleted"));
		return "redirect:/spring/stars/operator/home";
	}
	
	// ACCOUNT LOG
	@RequestMapping
    public String accountLog(ModelMap modelMap, 
    						 YukonUserContext userContext,
    						 AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        ArrayList<EventAccount> accountEvents = EventAccount.retrieveEventAccounts(accountInfoFragment.getAccountId());
        modelMap.addAttribute("accountEvents",accountEvents);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/account/accountLog.jsp";
    }
	
	// MISC MODEL ITEMS FOR ACCOUNT EDIT
	private void setupAccountEditModelMap(AccountInfoFragment accountInfoFragment, ModelMap modelMap, YukonUserContext userContext) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// substations
		List<Substation> substations = substationDao.getAllSubstationsByEnergyCompanyId(accountInfoFragment.getEnergyCompanyId());
		modelMap.addAttribute("substations", substations);
		
		// pageEditMode
		boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
		modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
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
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
	
	@Autowired
	public void setSubstationDao(SubstationDao substationDao) {
		this.substationDao = substationDao;
	}
}
