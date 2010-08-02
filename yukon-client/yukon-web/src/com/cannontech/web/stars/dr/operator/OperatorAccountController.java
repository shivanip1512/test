package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.Substation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.SubstationDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.cannontech.web.stars.dr.operator.importAccounts.service.AccountImportService;
import com.cannontech.web.stars.dr.operator.model.AccountImportData;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.validator.AccountGeneralValidator;
import com.cannontech.web.stars.dr.operator.validator.AccountImportDataValidator;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/account/*")
public class OperatorAccountController {

    private AccountEventLogService accountEventLogService;
	private OperatorGeneralSearchService operatorGeneralSearchService;
	private OperatorAccountService operatorAccountService;
	private StarsDatabaseCache starsDatabaseCache;
	private CustomerAccountDao customerAccountDao;
	private AccountService accountService;
	private AccountGeneralValidator accountGeneralValidator;
	private RolePropertyDao rolePropertyDao;
	private ECMappingDao ecMappingDao;
	private SubstationDao substationDao;
    private AccountImportDataValidator accountImportDataValidator;
    private AccountImportService accountImportService;
    private RecentResultsCache<AccountImportResult> recentResultsCache;
	
	// ACCOUNT IMPORT PAGE
	@RequestMapping
	public String accountImport(ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope, String processedBeforeCancel) {
	    if(!Boolean.parseBoolean(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser()))) {
	        throw new NotAuthorizedException("User not authorized to view this page.");
	    }
	    setupAccountImportModelMap(modelMap);
	    
	    if(StringUtils.isNotBlank(processedBeforeCancel)){
	        flashScope.setMessage(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountImport.canceledMessage", processedBeforeCancel), FlashScopeMessageType.WARNING);
	    }
	    
	    return "operator/account/accountImport.jsp";
	}

	// IMPORT ACCOUNTS
    @RequestMapping(method=RequestMethod.POST)
    public String uploadImportFiles(HttpServletRequest request, @ModelAttribute("accountImportData") AccountImportData accountImportData, 
                                BindingResult bindingResult,
                                ModelMap modelMap, 
                                YukonUserContext userContext,
                                FlashScope flashScope) {
        if(!Boolean.parseBoolean(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser()))) {
            throw new NotAuthorizedException("User not authorized to view this page.");
        }
        
        BulkFileUpload accountFileUpload = BulkFileUploadUtils.getBulkFileUpload(request, "accountImportFile");
        BulkFileUpload hardwareFileUpload = BulkFileUploadUtils.getBulkFileUpload(request, "hardwareImportFile");
        accountImportData.setAccountFile(accountFileUpload);
        accountImportData.setHardwareFile(hardwareFileUpload);
        
        List<MessageSourceResolvable> errorMessages = Lists.newArrayList();
        
        /* Check if the files are there and the email is valid */
        if(accountFileUpload.hasErrors() && hardwareFileUpload.hasErrors()) {
            errorMessages.add(new YukonMessageSourceResolvable("yukon.web.modules.operator.accountImport.error.noFiles"));
        }
        
        /* Validate Email Address */
        accountImportDataValidator.validate(accountImportData, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> validationErrorMessages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            errorMessages.addAll(validationErrorMessages);
        } 
        
        /* Validation Failed */
        if(!errorMessages.isEmpty()) {
            flashScope.setMessage(errorMessages, FlashScopeMessageType.ERROR);
            setupAccountImportModelMap(modelMap);
            return "operator/account/accountImport.jsp";
        }
        
        AccountImportResult result = initAccountImportResult(userContext.getYukonUser(), accountFileUpload, hardwareFileUpload, accountImportData.getEmail(), true);
        accountImportService.startAccountImport(result, userContext);
        
        modelMap.addAttribute("resultId", result.getResultId());
        modelMap.addAttribute("prescan", true);
        return "operator/account/accountImportResults.jsp";
    }
    
    @RequestMapping
    public ModelAndView importResult(ModelMap modelMap, String resultId) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        AccountImportResult result = recentResultsCache.getResult(resultId);
        mav.addObject("passed", !result.hasErrors());
        return mav;
    }
    
    @RequestMapping
    public String importErrors(ModelMap modelMap, String resultId) throws ServletException {
        AccountImportResult result = recentResultsCache.getResult(resultId);
        modelMap.addAttribute("importErrors", result.getErrors());
        return "operator/account/importErrors.jsp";
    }
    
    @RequestMapping(params="cancelImport")
    public String cancelImport(ModelMap modelMap, String resultId, boolean prescan, YukonUserContext userContext) {
        if(!Boolean.parseBoolean(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser()))) {
            throw new NotAuthorizedException("User not authorized to view this page.");
        }
        AccountImportResult result = recentResultsCache.getResult(resultId);
        result.cancel();
        if(!prescan) {
            modelMap.addAttribute("processedBeforeCancel", result.getPosition());
        }
        setupAccountImportModelMap(modelMap);
        return "redirect:accountImport";
    }
    
    // DO ACCOUNT IMPORT
    @RequestMapping
    public String doAccountImport(ModelMap modelMap, String resultId, YukonUserContext userContext, String cancel) throws ServletException {
        if(!Boolean.parseBoolean(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser()))) {
            throw new NotAuthorizedException("User not authorized to view this page.");
        }
        AccountImportResult prescanResult = recentResultsCache.getResult(resultId); 
        AccountImportResult result = initAccountImportResult(userContext.getYukonUser(), prescanResult.getAccountFileUpload(), prescanResult.getHardwareFileUpload(), prescanResult.getEmail(), false);
        accountImportService.startAccountImport(result, userContext);
        
        modelMap.addAttribute("resultId", result.getResultId());

        int hardwareLines = prescanResult.getHwLines() == null ? 0 : prescanResult.getHwLines().size() -1;
        int accountLines = prescanResult.getCustLines() == null ? 0 : prescanResult.getCustLines().size() -1;
            
        modelMap.addAttribute("callbackResult", result);
        modelMap.addAttribute("totalCount", hardwareLines + accountLines);
        modelMap.addAttribute("prescan", false);
        return "operator/account/accountImportResults.jsp";
    }
    
	// SEARCH
	@RequestMapping
    public String search(HttpServletRequest request,
    					Integer itemsPerPage, 
    					Integer page,
    					ModelMap modelMap, 
    					YukonUserContext userContext,
    					FlashScope flashScope) throws ServletRequestBindingException {
		
	    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, userContext.getYukonUser());
	    
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
		CustomerAccount customerAccount = customerAccountDao.getById(accountId);
		String currentAccountNumber = customerAccount.getAccountNumber();
		
		// UpdatableAccount
		UpdatableAccount updatableAccount = new UpdatableAccount();
		updatableAccount.setAccountNumber(currentAccountNumber);
		updatableAccount.setAccountDto(accountGeneral.getAccountDto());
		
		accountEventLogService.accountUpdateAttemptedByOperator(userContext.getYukonUser(),
		                                                        updatableAccount.getAccountNumber());
		
		// validate/update
		try {
			
			accountGeneralValidator.validate(accountGeneral, bindingResult);
			
			if (!bindingResult.hasErrors()) {
				accountService.updateAccount(updatableAccount, userContext.getYukonUser());
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
	    CustomerAccount customerAccount = customerAccountDao.getById(accountId);
	    
	    accountEventLogService.accountDeletionAttemptedByOperator(userContext.getYukonUser(),
	                                                              customerAccount.getAccountNumber());
	    
		accountService.deleteAccount(accountId, userContext.getYukonUser());
		
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
	
	private AccountImportResult initAccountImportResult(LiteYukonUser user, BulkFileUpload accountFileUpload, BulkFileUpload hardwareFileUpload, String email, boolean prescan) {
        AccountImportResult result = new AccountImportResult();
        result.setCurrentUser(user);
        result.setAccountFileUpload(accountFileUpload);
        result.setHardwareFileUpload(hardwareFileUpload);
        result.setEnergyCompany(starsDatabaseCache.getEnergyCompanyByUser(user));
        result.setEmail(email);
        result.setPrescan(prescan);
        
        String resultsId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        result.setResultId(resultsId);
        recentResultsCache.addResult(resultsId, result);
        return result;
    }
	
	private void setupAccountImportModelMap(ModelMap model) {
        AccountImportData data = new AccountImportData();
        model.addAttribute("accountImportData", data);
        model.addAttribute("accountFields", AccountImportFields.values());
        model.addAttribute("hardwareFields", HardwareImportFields.values());
    }
	
	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
	
	@Autowired
	public void setAccountImportDataValidator(AccountImportDataValidator accountImportDataValidator) {
	    this.accountImportDataValidator = accountImportDataValidator;
	}
	
	@Autowired
	public void setAccountImportService(AccountImportService accountImportService) {
	    this.accountImportService = accountImportService;
	}
	
	@Resource(name="accountImportResultsCache")
    public void setRecentResultsCache(RecentResultsCache<AccountImportResult> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}