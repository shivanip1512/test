package com.cannontech.web.stars.dr.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.Substation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.SubstationDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
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
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.UserUtils;
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
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.service.ResidentialLoginService;
import com.cannontech.web.stars.dr.operator.validator.AccountGeneralValidator;
import com.cannontech.web.stars.dr.operator.validator.AccountImportDataValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.util.JsonView;
import com.cannontech.web.util.TextView;
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
	private SubstationDao substationDao;
    private AccountImportDataValidator accountImportDataValidator;
    private AccountImportService accountImportService;
    private RecentResultsCache<AccountImportResult> recentResultsCache;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private AuthenticationService authenticationService;
    private ResidentialLoginService residentialLoginService;
    private TransactionOperations transactionTemplate;
    private LoginValidatorFactory loginValidatorFactory;
    private SystemEventLogService systemEventLogService;
    
    static private enum LoginModeEnum {
        CREATE,
        EDIT,
        VIEW;
    }
	
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

	// UPLOAD IMPORT FILES
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
        if(hardwareFileUpload.getFile() != null) {
            modelMap.addAttribute("showHardwareStats", true);
        }
        if(accountFileUpload.getFile() != null) {
            modelMap.addAttribute("showCustomerStats", true);
        }
        
        return "operator/account/accountImportResults.jsp";
    }
    
    // IMPORT RESULTS PAGE
    @RequestMapping
    public ModelAndView importResult(ModelMap modelMap, String resultId) throws ServletException {
        ModelAndView mav = new ModelAndView(new JsonView());
        AccountImportResult result = recentResultsCache.getResult(resultId);
        mav.addObject("passed", !result.hasErrors());
        return mav;
    }
    
    // IMPORT ERRORS PAGE
    @RequestMapping
    public String importErrors(ModelMap modelMap, String resultId) throws ServletException {
        AccountImportResult result = recentResultsCache.getResult(resultId);
        modelMap.addAttribute("importErrors", result.getErrors());
        return "operator/account/importErrors.jsp";
    }
    
    // CANCEL AN IMPORT
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
        
        if(result.getAccountFileUpload().getFile() != null) {
            modelMap.addAttribute("showCustomerStats", true);
        }
        
        return "operator/account/accountImportResults.jsp";
    }
    
	// SEARCH PAGE
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
	
	// NEW ACCOUNT PAGE
	@RequestMapping
    public String accountCreate(ModelMap modelMap, YukonUserContext userContext) throws ServletRequestBindingException {
	    
	    boolean hasOddForControlRole = rolePropertyDao.checkRole(YukonRole.ODDS_FOR_CONTROL, userContext.getYukonUser());
	    
	    /* AccountDto */
	    AccountDto accountDto = new AccountDto();
	    accountDto.setIsCommercial(false);

	    /* OperatorGeneralUiExtras */
	    OperatorGeneralUiExtras uiExtras = new OperatorGeneralUiExtras();
	    uiExtras.setHasOddsForControlRole(hasOddForControlRole);

	    /* LoginBackingBean */
	    LoginBackingBean loginBackingBean = new LoginBackingBean();
	    loginBackingBean.setLoginEnabled(LoginStatusEnum.ENABLED);

	    /* AccountGeneral */
	    AccountGeneral accountGeneral = new AccountGeneral();
	    accountGeneral.setAccountDto(new AccountDto());
	    accountGeneral.setOperatorGeneralUiExtras(uiExtras);
	    accountGeneral.setLoginBackingBean(loginBackingBean);

	    modelMap.addAttribute("accountGeneral", accountGeneral);
        
	    setupAccountCreationModelMap(modelMap, userContext.getYukonUser());
	    return "operator/account/account.jsp";
	}
	
	// CREATE ACCOUNT
	@RequestMapping(method=RequestMethod.POST, value="createAccount")
	public String createAccount(final @ModelAttribute("accountGeneral") AccountGeneral accountGeneral,
	                            BindingResult bindingResult,
	                            FlashScope flashScope,
	                            ModelMap modelMap, 
	                            HttpServletRequest request,
	                            final HttpSession session,
	                            final LiteYukonUser user) throws ServletRequestBindingException {
	    
	    /* Cancel Creation */
	    String cancelButton = ServletRequestUtils.getStringParameter(request, "cancelCreation");
	    if (cancelButton != null) {
            return "redirect:search";
        }
	    
	    LoginValidator loginValidator = loginValidatorFactory.getLoginValidator(yukonUserDao.getLiteYukonUser(UserUtils.USER_DEFAULT_ID));
	    final LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
	    
	    /* This role property forces the creation of a login with the creation of an account. */
	    final boolean createLogin = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_CREATE_LOGIN_FOR_ACCOUNT, user);

	    /* UpdatableAccount */
	    final UpdatableAccount updatableAccount = new UpdatableAccount();
	    updatableAccount.setAccountDto(accountGeneral.getAccountDto());
	    updatableAccount.setAccountNumber(accountGeneral.getAccountDto().getAccountNumber());

	    /* Validate and Create */
	    try {
            
            accountGeneralValidator.validate(accountGeneral, bindingResult);
            if(createLogin) {
                loginValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
            }
            
            if (!bindingResult.hasErrors()) {
                
                if(createLogin) {
                    /* Check to see if the user is trying to modify the default user */
                    checkEditingDefaultUser(accountGeneral.getLoginBackingBean().getUsername());
                }
	    
                /* Validation passed, proceed with creation */
    	        /* IMPORTANT NOTE HERE
    	         * 
    	         * The AccountDto does have login fields and can be used by the AccountSevice to create/edit/delete logins
    	         * because it was originally created for the BGE soap messaging service.  There was also a seperate operator page 
    	         * to create/edit/delete logins which has now been rolled into the account page.  For the account page we will
    	         * use the (refactored and moved) operator page login code, now moved to the ResidentialLoginService.  This was 
    	         * done because the page has slightly more options for logins than the messaging service does and to ensure
    	         * that the messaging service was not broken.  If you have set any of the login fields on the accountDto a new 
    	         * login will get created incorrectly, much pain and suffering will follow.
    	         * 
    	         * TLDR - make sure you never set the login fields on the AccountDto here and only use the login fields on the LoginBackingBean */
    	        Integer accountId = (Integer) transactionTemplate.execute(new TransactionCallback() {
    	            
    	            public Object doInTransaction(TransactionStatus status) {
    	                /* Create the account */
            	        int accountId = accountService.addAccount(updatableAccount, user);
            	        if(createLogin) {
            	            /* Create the login */
            	            residentialLoginService.createResidentialLogin(accountGeneral.getLoginBackingBean(), user, accountId, energyCompany.getEnergyCompanyId());
            	            /* Added Event Log Message */
            	        }
            	        EventUtils.logSTARSEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, 
            	                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_CREATED, accountId, session);
            	        return accountId;
    	            }
    	        });
    	        
    	        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.account.accountCreated"));
    	        modelMap.addAttribute("accountId", accountId);
    	        return "redirect:accountEdit";
            }
            
        } catch (AccountNumberUnavailableException e) {
            
            bindingResult.rejectValue("accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberUnavailable");
        
        } finally {
            
            if (bindingResult.hasErrors()) {
                
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                
                setupAccountCreationModelMap(modelMap, user);
                
                return "operator/account/account.jsp";
            }
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.account.accountCreated"));
        return "redirect:accountEdit";
	}
	
	// ACCOUNT EDIT PAGE
	@RequestMapping
    public String accountEdit(int accountId, ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfo) {
		
	    LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
	    List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
	    LiteYukonUser residentialUser = customerAccountDao.getYukonUserByAccountId(accountInfo.getAccountId());

	    /* AccountDto */
		AccountDto accountDto = accountService.getAccountDto(accountId, energyCompany.getEnergyCompanyId(), userContext);

		/* OperatorGeneralUiExtras */
		OperatorGeneralUiExtras operatorGeneralUiExtras = operatorAccountService.getOperatorGeneralUiExtras(accountId, userContext);
		
		/* LoginBackingBean */
		LoginBackingBean loginBackingBean = new LoginBackingBean();
        String userResidentialGroupName = yukonGroupDao.getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        loginBackingBean.setCustomerLoginGroupName(userResidentialGroupName);
        
        if (residentialUser.getUserID() == UserUtils.USER_DEFAULT_ID) {
            modelMap.addAttribute("loginMode", LoginModeEnum.CREATE);
            loginBackingBean.setLoginEnabled(LoginStatusEnum.ENABLED);
        } else {
            loginBackingBean.setUsername(residentialUser.getUsername());
            loginBackingBean.setLoginEnabled(residentialUser.getLoginStatus());
            modelMap.addAttribute("loginMode", LoginModeEnum.EDIT);
        }

        /* AccountGeneral */
        AccountGeneral accountGeneral = new AccountGeneral();
        accountGeneral.setAccountDto(accountDto);
        accountGeneral.setOperatorGeneralUiExtras(operatorGeneralUiExtras);
        accountGeneral.setLoginBackingBean(loginBackingBean);

        modelMap.addAttribute("accountGeneral", accountGeneral);

        setupAccountEditModelMap(accountInfo, modelMap, userContext, ecResidentialGroups, residentialUser);

        return "operator/account/account.jsp";
	}
	
	// GENERATE PASSWORD
    @RequestMapping(value="generatePassword")
    public TextView generatePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String generatedPassword = RandomStringUtils.randomAlphanumeric(6);
        return new TextView(generatedPassword);
    }
    
	// UPDATE ACCOUNT
	@RequestMapping(method=RequestMethod.POST, value="updateAccount")
    public String updateAccount(final @ModelAttribute("accountGeneral") AccountGeneral accountGeneral, 
								BindingResult bindingResult,
								final int accountId,
					    		ModelMap modelMap, 
					    		final YukonUserContext userContext,
					    		FlashScope flashScope,
					    		final AccountInfoFragment accountInfoFragment,
					    		HttpServletRequest request,
					    		final HttpSession session) throws ServletRequestBindingException {
	    
	    /* Verify the user has permission to edit accounts */
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(userContext.getYukonUser());
        List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        String currentAccountNumber = customerAccount.getAccountNumber();
	    
	    final String loginMode = ServletRequestUtils.getStringParameter(request, "loginMode");
        modelMap.addAttribute("loginMode", loginMode);
        final LiteYukonUser residentialUser = customerAccountDao.getYukonUserByAccountId(accountInfoFragment.getAccountId());
        String originalLoginGroup = yukonGroupDao.getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        
        /* 
         * Ignore the login fields if: 
         * 
         * The user doesn't have permission to edit logins 
         * OR if there is no login AND the user has not specified a user name
         * OR if there is a login AND the login fields were not changed.  
         */
        final boolean ignoreLogin = !hasEditLoginPrivileges(userContext.getYukonUser()) 
            || (residentialUser.getUserID() == UserUtils.USER_DEFAULT_ID && StringUtils.isBlank(accountGeneral.getLoginBackingBean().getUsername()))
            || (residentialUser.getUserID() != UserUtils.USER_DEFAULT_ID && 
                    (originalLoginGroup == null || 
                     !didLoginChange(residentialUser, accountGeneral.getLoginBackingBean(), originalLoginGroup)));
		
		/* UpdatableAccount */
		final UpdatableAccount updatableAccount = new UpdatableAccount();
		updatableAccount.setAccountNumber(currentAccountNumber);
		updatableAccount.setAccountDto(accountGeneral.getAccountDto());
		
		accountEventLogService.accountUpdateAttemptedByOperator(userContext.getYukonUser(), updatableAccount.getAccountNumber());
		
		if(!ignoreLogin) {
		    systemEventLogService.loginChangeAttemptedByOperator(userContext.getYukonUser(), residentialUser.getUsername());
		}
		LoginValidator loginValidator = loginValidatorFactory.getLoginValidator(residentialUser);
		
		/* Validate and Update */
		try {
			
			accountGeneralValidator.validate(accountGeneral, bindingResult);
			if(!ignoreLogin) {
			    loginValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
			    
			    /* Check to see if the user is trying to modify the default user */
                checkEditingDefaultUser(accountGeneral.getLoginBackingBean().getUsername());
			}
			
			if (!bindingResult.hasErrors()) {
			    
                transactionTemplate.execute(new TransactionCallback() {
                    
                    public Object doInTransaction(TransactionStatus status) {
                        /* see IMPORTANT NOTE comment in createAccount method, same rules apply here */
        				accountService.updateAccount(updatableAccount, userContext.getYukonUser());
        				operatorAccountService.updateAccount(accountId, accountGeneral.getOperatorGeneralUiExtras());
        				if(!ignoreLogin) {
                            if (LoginModeEnum.CREATE.equals(LoginModeEnum.valueOf(loginMode))) {
            
                                residentialLoginService.createResidentialLogin(accountGeneral.getLoginBackingBean(), 
                                                       userContext.getYukonUser(), 
                                                       accountInfoFragment.getAccountId(), 
                                                       accountInfoFragment.getEnergyCompanyId());
                            } else {
                                residentialLoginService.updateResidentialLogin(accountGeneral.getLoginBackingBean(), 
                                                                               userContext, 
                                                                               residentialUser, 
                                                                               accountInfoFragment.getEnergyCompanyId());
                            }
                            /* Added Event Log Message */
                            EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                                     YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, accountInfoFragment.getAccountId(), session);
        				}
                        return null;
                    }
                });
			}
			
		} catch (AccountNumberUnavailableException e) {
			
			bindingResult.rejectValue("accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberUnavailable");
		
		} finally {
			setupAccountEditModelMap(accountInfoFragment, modelMap, userContext, ecResidentialGroups, residentialUser);

			if (bindingResult.hasErrors()) {
				
				List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
				flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
				
				return "operator/account/account.jsp";
			} 
		}
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.account.accountUpdated"));
		return "redirect:accountEdit";
	}
	
	// DELETE LOGIN
    @RequestMapping
    public String deleteLogin(String loginMode,
                               ModelMap modelMap, 
                               YukonUserContext userContext,
                               FlashScope flashScope,
                               AccountInfoFragment accountInfoFragment) {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        LiteYukonUser custYukonUser = customerAccountDao.getYukonUserByAccountId(accountInfoFragment.getAccountId());

        LoginModeEnum loginModeEnum = LoginModeEnum.valueOf(loginMode);
        if (LoginModeEnum.EDIT.equals(loginModeEnum)) {
            yukonUserDao.deleteUser(custYukonUser.getUserID());
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.account.loginDeleted"));
        
        modelMap.addAttribute("accountId", accountInfoFragment.getAccountId());
        return "redirect:accountEdit";
    }
	
	// DELETE ACCOUNT
	@RequestMapping
    public String deleteAccount(int accountId,
    							ModelMap modelMap, 
    							YukonUserContext userContext,
    							FlashScope flashScope) throws ServletRequestBindingException {
	    CustomerAccount customerAccount = customerAccountDao.getById(accountId);
	    accountEventLogService.accountDeletionAttemptedByOperator(userContext.getYukonUser(), customerAccount.getAccountNumber());
		accountService.deleteAccount(accountId, userContext.getYukonUser());
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.account.accountDeleted"));
		return "redirect:/spring/stars/operator/home";
	}
	
	// ACCOUNT LOG
	@RequestMapping
    public String accountLog(ModelMap modelMap, YukonUserContext userContext, AccountInfoFragment accountInfoFragment) {
        
        ArrayList<EventAccount> accountEvents = EventAccount.retrieveEventAccounts(accountInfoFragment.getAccountId());
        modelMap.addAttribute("accountEvents",accountEvents);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/account/accountLog.jsp";
    }
	
	// ACCOUNT GENERAL WRAPPER
	public static class AccountGeneral {
		
		private AccountDto accountDto = new AccountDto();
		private OperatorGeneralUiExtras operatorGeneralUiExtras = new OperatorGeneralUiExtras();
		private LoginBackingBean loginBackingBean = new LoginBackingBean();
		
		public AccountDto getAccountDto() {
			return accountDto;
		}
		public void setAccountDto(AccountDto accountDto) {
			this.accountDto = accountDto;
		}
		public OperatorGeneralUiExtras getOperatorGeneralUiExtras() {
			return operatorGeneralUiExtras;
		}
		public void setOperatorGeneralUiExtras(OperatorGeneralUiExtras operatorGeneralUiExtras) {
			this.operatorGeneralUiExtras = operatorGeneralUiExtras;
		}
        public void setLoginBackingBean(LoginBackingBean loginBackingBean) {
            this.loginBackingBean = loginBackingBean;
        }
        public LoginBackingBean getLoginBackingBean() {
            return loginBackingBean;
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
	
	private void setupAccountCreationModelMap(ModelMap modelMap, LiteYukonUser user) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
        List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        boolean showLoginSection = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_CREATE_LOGIN_FOR_ACCOUNT, user);
        
        List<Substation> substations = substationDao.getAllSubstationsByEnergyCompanyId(energyCompany.getEnergyCompanyId());
        modelMap.addAttribute("substations", substations);

        modelMap.addAttribute("loginMode", LoginModeEnum.CREATE);
        modelMap.addAttribute("supportsPasswordSet", true);
        modelMap.addAttribute("energyCompanyId", energyCompany.getEnergyCompanyId());
        modelMap.addAttribute("showLoginSection", showLoginSection);
        modelMap.addAttribute("ecResidentialGroups", ecResidentialGroups);
        modelMap.addAttribute("mode", PageEditMode.CREATE);
    }
	
	private void setupAccountEditModelMap(AccountInfoFragment accountInfoFragment, 
	                                      ModelMap modelMap, 
	                                      YukonUserContext userContext,
	                                      List<LiteYukonGroup> ecResidentialGroups,
	                                      LiteYukonUser residentialUser) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        // substations
        List<Substation> substations = substationDao.getAllSubstationsByEnergyCompanyId(accountInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("substations", substations);
        
        // pageEditMode
        boolean allowAccountEditing = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        modelMap.addAttribute("mode", allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
        
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        
        modelMap.addAttribute("showLoginSection", hasEditLoginPrivileges(userContext.getYukonUser()));

        modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(residentialUser.getAuthType()));
        
        modelMap.addAttribute("ecResidentialGroups", ecResidentialGroups);
        if (residentialUser.getUserID() == UserUtils.USER_DEFAULT_ID) {
            modelMap.addAttribute("loginMode", LoginModeEnum.CREATE);
        } else {
            modelMap.addAttribute("loginMode", LoginModeEnum.EDIT);
        }
    }
	
	private boolean hasEditLoginPrivileges(LiteYukonUser user) {
        return rolePropertyDao.checkAnyProperties(user, 
                                                  YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, 
                                                  YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD);
    }
	
	private void checkEditingDefaultUser(String username) {
        LiteYukonUser defaultUserCheck = yukonUserDao.getLiteYukonUser(username);
        
        if (defaultUserCheck != null && defaultUserCheck.getUserID() == UserUtils.USER_DEFAULT_ID) {
            throw new RuntimeException("You cannot edit the the default user.");
        }
    }
	
	/**
     * Returns true if any of the login fields were changed by the user.
     * @param residentialUser
     * @param loginBackingBean
     * @param originalLoginGroup
     * @return true if any of the login fields were changed.
     */
    private boolean didLoginChange(LiteYukonUser residentialUser, LoginBackingBean loginBackingBean, String originalLoginGroup) {
        String previousUsername = residentialUser.getUsername();
        
        boolean didNotChange = previousUsername.equals(loginBackingBean.getUsername())
            && StringUtils.isBlank(loginBackingBean.getPassword1()) 
            && StringUtils.isBlank(loginBackingBean.getPassword2())
            && originalLoginGroup.equals(loginBackingBean.getCustomerLoginGroupName())
            && residentialUser.getLoginStatus() == loginBackingBean.getLoginStatus();
        
        return !didNotChange;
    }
	
	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
	
	@Autowired
    public void setSystemEventLogService(SystemEventLogService systemEventLogService) {
        this.systemEventLogService = systemEventLogService;
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
	
	@Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
	
	@Autowired
	public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
	
	@Autowired
	public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
	
	@Autowired
	public void setResidentialLoginService(ResidentialLoginService residentialLoginService) {
        this.residentialLoginService = residentialLoginService;
    }
	
	@Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
	
	@Autowired
	public void setLoginValidatorFactory(LoginValidatorFactory loginValidatorFactory) {
        this.loginValidatorFactory = loginValidatorFactory;
    }
	
	@Resource(name="accountImportResultsCache")
    public void setRecentResultsCache(RecentResultsCache<AccountImportResult> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
	
}