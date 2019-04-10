package com.cannontech.web.stars.dr.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.Substation;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.EventAccount;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
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
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.bulk.util.BulkFileUpload;
import com.cannontech.web.bulk.util.BulkFileUploadUtils;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.login.model.Login;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;
import com.cannontech.web.stars.dr.operator.importAccounts.service.AccountImportService;
import com.cannontech.web.stars.dr.operator.model.AccountImportData;
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.dr.operator.service.ResidentialLoginService;
import com.cannontech.web.stars.dr.operator.validator.AccountGeneralValidator;
import com.cannontech.web.stars.dr.operator.validator.AccountImportDataValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginPasswordValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginUsernameValidator;
import com.cannontech.web.stars.dr.operator.validator.LoginValidatorFactory;
import com.cannontech.web.util.SessionUtil;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/account/*")
public class OperatorAccountController {

    private RecentResultsCache<AccountImportResult> recentResultsCache;
    private static final String baseKey = "yukon.web.modules.operator.";
    
    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountGeneralValidator accountGeneralValidator;
    @Autowired private AccountImportDataValidator accountImportDataValidator;
    @Autowired private AccountImportService accountImportService;
    @Autowired private AccountService accountService;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private LoginValidatorFactory loginValidatorFactory;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private OperatorGeneralSearchService operatorGeneralSearchService;
    @Autowired private ResidentialLoginService residentialLoginService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private SubstationDao substationDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private TransactionOperations transactionTemplate;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonUserDao userDao;
    @Autowired private UsersEventLogService usersEventLogService;
    
    static private enum LoginModeEnum {
        CREATE,
        EDIT,
        VIEW;
    }
    
    // ACCOUNT IMPORT PAGE
    @RequestMapping("accountImport")
    public String accountImport(ModelMap modelMap, YukonUserContext userContext, FlashScope flashScope, 
            String processedBeforeCancel) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser());
        setupAccountImportModelMap(modelMap);
        
        if (StringUtils.isNotBlank(processedBeforeCancel)) {
            flashScope.setMessage(new YukonMessageSourceResolvable(baseKey + "accountImport.canceledMessage", 
                    processedBeforeCancel), FlashScopeMessageType.WARNING);
        }
        
        return "operator/account/accountImport.jsp";
    }
    
    // UPLOAD IMPORT FILES
    @RequestMapping(value="uploadImportFiles", method=RequestMethod.POST)
    public String uploadImportFiles(HttpServletRequest request, 
            @ModelAttribute("accountImportData") AccountImportData accountImportData, 
            BindingResult bindingResult,
            ModelMap modelMap, 
            YukonUserContext userContext,
            FlashScope flashScope) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser());
        
        BulkFileUpload accountFileUpload =
            BulkFileUploadUtils.getBulkFileUpload(request, "accountImportFile");
        BulkFileUpload hardwareFileUpload =
            BulkFileUploadUtils.getBulkFileUpload(request, "hardwareImportFile");
        accountImportData.setAccountFile(accountFileUpload);
        accountImportData.setHardwareFile(hardwareFileUpload);
        
        List<MessageSourceResolvable> errorMessages = Lists.newArrayList();
        
        /* Check if the files are there and the email is valid */
        if (accountFileUpload.hasErrors() && hardwareFileUpload.hasErrors()) {
            errorMessages.add(new YukonMessageSourceResolvable(accountFileUpload.getErrors().get(0)));
        }
        
        /* Validate Email Address */
        accountImportDataValidator.validate(accountImportData, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> validationErrorMessages = 
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
            errorMessages.addAll(validationErrorMessages);
        } 
        
        /* Validation Failed */
        if (!errorMessages.isEmpty()) {
            flashScope.setMessage(errorMessages, FlashScopeMessageType.ERROR);
            setupAccountImportModelMap(modelMap);
            return "operator/account/accountImport.jsp";
        }
        
        AccountImportResult result = initAccountImportResult(userContext.getYukonUser(), accountFileUpload, 
                hardwareFileUpload, accountImportData.getEmail(), true);
        accountImportService.startAccountImport(result, userContext);
        
        modelMap.addAttribute("resultId", result.getResultId());
        modelMap.addAttribute("prescan", true);
        if (hardwareFileUpload.getFile() != null) {
            modelMap.addAttribute("showHardwareStats", true);
            modelMap.addAttribute("hardwareFileName", result.getHardwareFileUpload().getName());
        }
        if (accountFileUpload.getFile() != null) {
            modelMap.addAttribute("showCustomerStats", true);
            modelMap.addAttribute("accountFileName", result.getAccountFileUpload().getName());
        }
        
        return "operator/account/accountImportResults.jsp";
    }
    
    @RequestMapping("imports")
    @CheckRoleProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT)
    public String imports(ModelMap model, YukonUserContext userContext) {
        
        boolean isEcOperator = ecDao.isEnergyCompanyOperator(userContext.getYukonUser());
        if (!isEcOperator) {
            throw NotAuthorizedException.ecOperator(userContext.getYukonUser());
        }
        
        List<AccountImportResult> completed = new ArrayList<>();
        for (AccountImportResult result : recentResultsCache.getCompleted()) {
            if (!result.isPrescan()) {
                completed.add(result);
            }
        }
        List<AccountImportResult> pending = new ArrayList<>();
        for (AccountImportResult result : recentResultsCache.getPending()) {
            if (!result.isPrescan()) {
                pending.add(result);
            }
        }
        model.addAttribute("completed", completed);
        model.addAttribute("pending", pending);
        
        return "operator/account/imports.jsp";
    }
    
    @RequestMapping("importResult")
    @ResponseBody
    public BooleanNode importResult(String resultId) {
        AccountImportResult result = recentResultsCache.getResult(resultId);
        return BooleanNode.valueOf(!result.hasErrors());
    }
    
    // IMPORT ERRORS PAGE
    @RequestMapping("importErrors")
    public String importErrors(ModelMap modelMap, String resultId) {
        
        AccountImportResult result = recentResultsCache.getResult(resultId);
        modelMap.addAttribute("importErrors", result.getErrors());
        
        return "operator/account/importErrors.jsp";
    }
    
    // CANCEL AN IMPORT
    @RequestMapping(value="doAccountImport", params="cancelImport")
    public String cancelImport(ModelMap modelMap, String resultId, boolean prescan, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser());
        AccountImportResult result = recentResultsCache.getResult(resultId);
        result.cancel();
        if (!prescan) {
            result.setStopTime(new Instant());
            modelMap.addAttribute("processedBeforeCancel", result.getPosition());
        }
        setupAccountImportModelMap(modelMap);
        
        return "redirect:accountImport";
    }
    
    // DO ACCOUNT IMPORT
    @RequestMapping("doAccountImport")
    public String doAccountImport(ModelMap modelMap, String resultId, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser());
        AccountImportResult prescanResult = recentResultsCache.getResult(resultId); 
        AccountImportResult result = initAccountImportResult(userContext.getYukonUser(), 
                prescanResult.getAccountFileUpload(), prescanResult.getHardwareFileUpload(), 
                prescanResult.getEmail(), false);
        accountImportService.startAccountImport(result, userContext);
        
        modelMap.addAttribute("resultId", result.getResultId());
        
        int hardwareLines = prescanResult.getHwLines() == null ? 0 : prescanResult.getHwLines().size() -1;
        int accountLines = prescanResult.getCustLines() == null ? 0 : prescanResult.getCustLines().size() -1;
        int totalCount = hardwareLines + accountLines;
        result.setTotalCount(totalCount);
            
        modelMap.addAttribute("callbackResult", result);
        modelMap.addAttribute("totalCount", totalCount);
        modelMap.addAttribute("prescan", false);
        
        if (result.getAccountFileUpload().getFile() != null) {
            modelMap.addAttribute("showCustomerStats", true);
        }
        
        return "operator/account/accountImportResults.jsp";
    }
    
    // VIEW ACCOUNT IMPORT
    @RequestMapping("imports/{resultId}")
    public String view(ModelMap modelMap, @PathVariable String resultId, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_IMPORT_CUSTOMER_ACCOUNT, userContext.getYukonUser());
        AccountImportResult result = recentResultsCache.getResult(resultId); 
        
        modelMap.addAttribute("resultId", result.getResultId());
        modelMap.addAttribute("callbackResult", result);
        modelMap.addAttribute("totalCount", result.getTotalCount());
        modelMap.addAttribute("prescan", false);
        
        if (result.getAccountFileUpload().getFile() != null) {
            modelMap.addAttribute("showCustomerStats", true);
        }
        
        return "operator/account/accountImportResults.jsp";
    }
    
    // SEARCH PAGE
    @RequestMapping("search")
    public String search(HttpServletRequest request,
                        @DefaultItemsPerPage(25) PagingParameters paging,
                        ModelMap modelMap, 
                        YukonUserContext userContext,
                        FlashScope flashScope) throws ServletRequestBindingException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, userContext.getYukonUser());
        
        String searchByStr = ServletRequestUtils.getStringParameter(request, "searchBy", null);
        
        // no search
        if (StringUtils.isBlank(searchByStr) || !ecDao.isEnergyCompanyOperator(userContext.getYukonUser())) {
            modelMap.addAttribute("accountSearchResultHolder", AccountSearchResultHolder.emptyAccountSearchResultHolder());
            modelMap.addAttribute("operatorAccountSearchBys", OperatorAccountSearchBy.values());
            return "operator/account/accountList.jsp";
        }
        
        // searchBy, searchValue
        OperatorAccountSearchBy searchBy = OperatorAccountSearchBy.valueOf(searchByStr);
        String searchValue = ServletRequestUtils.getStringParameter(request, "searchValue", null);
        
        // it is important for searching to use the energyCompany of the user
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        final LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        AccountSearchResultHolder search = operatorGeneralSearchService.customerAccountSearch(
                searchBy, searchValue, paging.getStartIndex(), paging.getItemsPerPage(), energyCompany, userContext);
        
        boolean adminManageMembers = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, 
                userContext.getYukonUser());
        modelMap.addAttribute("searchMembers", adminManageMembers && energyCompany.hasChildEnergyCompanies());
        
        // account edit
        if (search.isSingleResult() && !search.isHasWarning()) {
            
            AccountSearchResult accountSearchResult = search.getAccountSearchResults().getResultList().get(0);
            modelMap.addAttribute("accountId", accountSearchResult.getAccountId());
            
            return "redirect:view";
        }
        
        // account list
        if (search.isHasWarning()) {
            flashScope.setWarning(Collections.singletonList(search.getWarning()));
        }
        Object[] searchResultTitleArguments = new Object[] {
                new YukonMessageSourceResolvable(searchBy.getFormatKey()), 
                searchValue
        };
        modelMap.addAttribute("searchResultTitleArguments", searchResultTitleArguments);
        modelMap.addAttribute("accountSearchResultHolder", search);
        modelMap.addAttribute("operatorAccountSearchBys", OperatorAccountSearchBy.values());
        
        return "operator/account/accountList.jsp";
    }
    
    // NEW ACCOUNT PAGE
    @RequestMapping("accountCreate")
    public String accountCreate(ModelMap modelMap, YukonUserContext userContext) {
        
        if (ecDao.isEnergyCompanyOperator(userContext.getYukonUser())) {
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD, userContext.getYukonUser());
            
            boolean hasOddForControlRole = rolePropertyDao.checkRole(YukonRole.ODDS_FOR_CONTROL, 
                    userContext.getYukonUser());
            
            /* AccountDto */
            AccountDto accountDto = new AccountDto();
            accountDto.setIsCommercial(false);
            
            /* OperatorGeneralUiExtras */
            OperatorGeneralUiExtras uiExtras = new OperatorGeneralUiExtras();
            uiExtras.setHasOddsForControlRole(hasOddForControlRole);
            
            /* LoginBackingBean */
            Login loginBackingBean = new Login();
            loginBackingBean.setLoginEnabled(LoginStatusEnum.ENABLED);
            
            /* AccountGeneral */
            AccountGeneral accountGeneral = new AccountGeneral();
            accountGeneral.setAccountDto(new AccountDto());
            accountGeneral.setOperatorGeneralUiExtras(uiExtras);
            accountGeneral.setLoginBackingBean(loginBackingBean);
            
            modelMap.addAttribute("accountGeneral", accountGeneral);
            
            setupAccountCreationModelMap(modelMap, userContext.getYukonUser());
        } else {
            modelMap.addAttribute("mode", PageEditMode.CREATE);
        }
        
        return "operator/account/account.jsp";
    }
    
    // CREATE ACCOUNT
    @RequestMapping(method=RequestMethod.POST, value="createAccount")
    public String createAccount(final @ModelAttribute AccountGeneral accountGeneral, BindingResult bindingResult,
            FlashScope flashScope, ModelMap model, final HttpSession session, final LiteYukonUser user)
            throws ServletRequestBindingException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_NEW_ACCOUNT_WIZARD, user);
        
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(null);
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(null);
        
        final int ecId = ecDao.getEnergyCompanyByOperator(user).getId();
        
        AccountDto accountDto = accountGeneral.getAccountDto();
        if (accountGeneral.getOperatorGeneralUiExtras().isUsePrimaryAddressForBilling()) {
            accountDto.setBillingAddress(accountDto.getStreetAddress());
        }
        
        /* UpdatableAccount */
        final UpdatableAccount updatableAccount = new UpdatableAccount();
        updatableAccount.setAccountDto(accountDto);
        updatableAccount.setAccountNumber(accountDto.getAccountNumber());
        /* Validate and Create */
        try {
            
            accountGeneralValidator.validate(accountGeneral, bindingResult);
            /* This role property forces the creation of a login with the creation of an account. */
            boolean isAutoCreateLogin = ecSettingDao.getBoolean(EnergyCompanySettingType.AUTO_CREATE_LOGIN_FOR_ACCOUNT, 
                    ecId);
            
            final boolean createLogin = isAutoCreateLogin
                    && (StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getPassword1()) 
                            || StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getPassword2())
                            || StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getUsername()));
            if (createLogin) {
                    bindingResult.pushNestedPath("loginBackingBean");
                    usernameValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
                    passwordValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
                    bindingResult.popNestedPath();
            }
            
            if (!bindingResult.hasErrors()) {
                
                if (createLogin) {
                    /* Check to see if the user is trying to modify the default user. */
                    checkEditingDefaultUser(accountGeneral.getLoginBackingBean().getUsername());
                }
                
                /* Validation passed, proceed with creation.
                 * 
                 * IMPORTANT NOTE HERE:
                 * The AccountDto does have login fields and can be used by the AccountSevice to create/edit/delete logins
                 * because it was originally created for the BGE soap messaging service.  There was also a separate 
                 * operator page to create/edit/delete logins which has now been rolled into the account page. 
                 * For the account page we will use the (refactored and moved) operator page login code, 
                 * now moved to the ResidentialLoginService.  This was done because the page has slightly more options 
                 * for logins than the messaging service does and to ensure that the messaging service was not broken. 
                 * If you have set any of the login fields on the accountDto a new login will get created incorrectly, 
                 * much pain and suffering will follow.
                 * 
                 * TLDR - Make sure you never set the login fields on the AccountDto here and only 
                 *        use the login fields on the LoginBackingBean. */
                Integer accountId = transactionTemplate.execute(new TransactionCallback<Integer>() {
                    
                    @Override
                    public Integer doInTransaction(TransactionStatus status) {
                        /* Create the account */
                        int accountId = operatorAccountService.addAccount(updatableAccount, user, 
                                accountGeneral.getOperatorGeneralUiExtras());
                        if (createLogin) {
                            /* Create the login */
                            residentialLoginService.createResidentialLogin(accountGeneral.getLoginBackingBean(), user, 
                                    accountId, ecId);
                            /* Added Event Log Message */
                        }
                        int userId = SessionUtil.getParentLoginUserId(session, user.getUserID());
                        EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                                 YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_CREATED, accountId);
                        return accountId;
                    }
                });
                
                flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "account.accountCreated"));
                model.addAttribute("accountId", accountId);
                
                return "redirect:view";
            }
            
        } catch (AccountNumberUnavailableException e) {
            bindingResult.rejectValue("accountDto.accountNumber", 
                    baseKey + "accountGeneral.accountDto.accountNumber.accountNumberUnavailable");
        } finally {
            
            if (bindingResult.hasErrors()) {
                
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                
                setupAccountCreationModelMap(model, user);
                
                return "operator/account/account.jsp";
            }
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "account.accountCreated"));
        return "redirect:view";
    }
    
    // ACCOUNT VIEW PAGE
    @RequestMapping("view")
    public String view(int accountId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        setupAccountPage(model, context, fragment, accountId);
        
        return "operator/account/account.jsp";
    }
    
    // ACCOUNT EDIT PAGE
    @RequestMapping("edit")
    public String edit(int accountId, ModelMap model, YukonUserContext context, AccountInfoFragment fragment) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, context.getYukonUser());
        model.addAttribute("mode", PageEditMode.EDIT);
        setupAccountPage(model, context, fragment, accountId);
        
        return "operator/account/account.jsp";
    }
    
    private void setupAccountPage(ModelMap model, YukonUserContext context, AccountInfoFragment fragment, int accountId) {
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(context.getYukonUser());
        List<LiteUserGroup> ecResidentialUserGroups = ecMappingDao.getResidentialUserGroups(energyCompany.getId());
        LiteYukonUser residentialUser = customerAccountDao.getYukonUserByAccountId(accountId);
        
        buildAccountDto(model, context, accountId, residentialUser, energyCompany);
        
        setupAccountModel(fragment, model, context, ecResidentialUserGroups, residentialUser);
    }
    
    private void buildAccountDto(ModelMap model, YukonUserContext context, int accountId, 
                                 LiteYukonUser residentialUser, EnergyCompany energyCompany) {
        
        /* AccountDto */
        AccountDto accountDto = accountService.getAccountDto(accountId, energyCompany.getId(), context);
        
        /* OperatorGeneralUiExtras */
        OperatorGeneralUiExtras extras = operatorAccountService.getOperatorGeneralUiExtras(accountId, context);
        
        /* LoginBackingBean */
        Login login = new Login();
        if (residentialUser.getUserGroupId() != null) {
            LiteUserGroup userResidentialUserGroup= userGroupDao.getLiteUserGroup(residentialUser.getUserGroupId());
            if (userResidentialUserGroup != null) {
                login.setUserGroupName(userResidentialUserGroup.getUserGroupName());
            }
        }
        
        if (residentialUser.getUserID() == UserUtils.USER_NONE_ID) {
            model.addAttribute("loginMode", LoginModeEnum.CREATE);
            login.setLoginEnabled(LoginStatusEnum.ENABLED);
        } else {
            login.setUsername(residentialUser.getUsername());
            login.setLoginEnabled(residentialUser.getLoginStatus());
            model.addAttribute("loginMode", LoginModeEnum.EDIT);
        }

        /* AccountGeneral */
        AccountGeneral accountGeneral = new AccountGeneral();
        accountGeneral.setAccountDto(accountDto);
        accountGeneral.setOperatorGeneralUiExtras(extras);
        accountGeneral.setLoginBackingBean(login);
        if (!StringUtils.isEmpty(login.getUsername())) {
            model.addAttribute("login", login);
        }
        
        model.addAttribute("accountGeneral", accountGeneral);
    }
    
    /*
     * The residentialLoginService authenticates the user for updating passwords on this account
     */
    @RequestMapping(value="updatePassword", method=RequestMethod.POST)
    public @ResponseBody Map<String, ? extends Object> updatePassword(
            final @ModelAttribute("login") Login login,
            BindingResult bindingResult,
            final YukonUserContext userContext,
            final AccountInfoFragment accountInfoFragment,
            HttpServletRequest request,
            HttpServletResponse response,
            final HttpSession session) throws ServletRequestBindingException{
        
        /* setup the required vars to determine if we can/should update the password for this login */
        LiteYukonUser residentialUser = customerAccountDao.getYukonUserByAccountId(accountInfoFragment.getAccountId());
        String loginMode = ServletRequestUtils.getStringParameter(request, "loginMode");
        
        /* Make sure the passwords match */
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(residentialUser);
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(residentialUser);
        passwordValidator.validate(login, bindingResult);
        usernameValidator.validate(login, bindingResult);
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        /* 
         * Update the password if: 
         * 
         * the user is not the default user
         * the login is being edited (not created or viewed)
         */
        if (residentialUser.getUserID() != UserUtils.USER_NONE_ID 
            && LoginModeEnum.EDIT.equals(LoginModeEnum.valueOf(loginMode))
            && !bindingResult.hasErrors()) {
            systemEventLogService.loginChangeAttempted(userContext.getYukonUser(), residentialUser.getUsername(), 
                    EventSource.OPERATOR);
            
            /* ensure that we are only updating passwords on existing accounts */
            residentialLoginService.updateResidentialPassword(login, userContext, residentialUser);
          
            /* Added Event Log Message */
            int userId = SessionUtil.getParentLoginUserId(session, userContext.getYukonUser().getUserID());
            EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                     YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, 
                                     accountInfoFragment.getAccountId());
            
            /* tell the browser this action passed */
            response.setStatus(HttpServletResponse.SC_OK);
            return Collections.singletonMap("flash", 
                    accessor.getMessage("yukon.web.changelogin.message.LOGIN_PASSWORD_CHANGED"));
        }
        
        /* tell the browser there was a problem */
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        Map<String, String> errorJSON = new HashMap<>();
        for (Object object : bindingResult.getAllErrors()) {
            if (object instanceof FieldError) {
                FieldError fieldError = (FieldError) object;
                errorJSON.put(fieldError.getField(), 
                        accessor.getMessage(fieldError.getCode(), fieldError.getArguments()));
            }
        }
        return Collections.singletonMap("fieldErrors", errorJSON);
    }
    
    // UPDATE ACCOUNT
    @RequestMapping(method=RequestMethod.POST, value="updateAccount")
    public String updateAccount(final @ModelAttribute("accountGeneral") AccountGeneral accountGeneral, 
            BindingResult bindingResult, final int accountId, ModelMap modelMap,  final YukonUserContext userContext, 
            FlashScope flashScope, final AccountInfoFragment accountInfoFragment, HttpServletRequest request,
            final HttpSession session) throws ServletRequestBindingException {
        
        /* Verify the user has permission to edit accounts */
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(userContext.getYukonUser());
        List<LiteUserGroup> ecResidentialUserGroups = ecMappingDao.getResidentialUserGroups(energyCompany.getId());
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        String currentAccountNumber = customerAccount.getAccountNumber();
        
        final String loginMode = ServletRequestUtils.getStringParameter(request, "loginMode");
        modelMap.addAttribute("loginMode", loginMode);
        final LiteYukonUser residentialUser = customerAccountDao.getYukonUserByAccountId(accountId);
        
        LiteUserGroup originalUserGroup = null;
        if (residentialUser.getUserGroupId() != null) {
            originalUserGroup = userGroupDao.getLiteUserGroup(residentialUser.getUserGroupId());
        }
        
        /* 
         * Ignore the login fields if: 
         * 
         * The user doesn't have permission to edit logins 
         * OR if there is no login AND the user has not specified a user name
         * OR if there is a login AND the login fields were not changed.  
         */
        boolean hasEditLoginPrivileges = hasEditLoginPrivileges(userContext.getYukonUser());
        final boolean ignoreLogin = !hasEditLoginPrivileges
            || (residentialUser.getUserID() == UserUtils.USER_NONE_ID 
                && StringUtils.isBlank(accountGeneral.getLoginBackingBean().getUsername()))
            || (residentialUser.getUserID() != UserUtils.USER_NONE_ID && originalUserGroup != null 
                && !didLoginChange(residentialUser, accountGeneral.getLoginBackingBean(), 
                        originalUserGroup.getUserGroupName()));
        
        /* UpdatableAccount */
        final UpdatableAccount updatableAccount = new UpdatableAccount();
        updatableAccount.setAccountNumber(currentAccountNumber);
        updatableAccount.setAccountDto(accountGeneral.getAccountDto());
        
        accountEventLogService.accountUpdateAttempted(userContext.getYukonUser(), updatableAccount.getAccountNumber(), 
                EventSource.OPERATOR);
        
        if (!ignoreLogin) {
            systemEventLogService.loginChangeAttempted(userContext.getYukonUser(), residentialUser.getUsername(), 
                    EventSource.OPERATOR);
        }
        LoginPasswordValidator passwordValidator = loginValidatorFactory.getPasswordValidator(residentialUser);
        LoginUsernameValidator usernameValidator = loginValidatorFactory.getUsernameValidator(residentialUser);
        
        /* Validate and Update */
        try {
            accountGeneralValidator.validate(accountGeneral, bindingResult);
            // create new login validation - validate if one of the fields (password fields or username) is entered
            if (hasEditLoginPrivileges && residentialUser.getUserID() == UserUtils.USER_NONE_ID 
                    && (StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getPassword1()) 
                    || StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getPassword2())
                    || StringUtils.isNotEmpty(accountGeneral.getLoginBackingBean().getUsername()))) {
                bindingResult.pushNestedPath("loginBackingBean");
                usernameValidator.validate(accountGeneral.getLoginBackingBean(),bindingResult);
                passwordValidator.validate(accountGeneral.getLoginBackingBean(),
                                bindingResult);
                bindingResult.popNestedPath();
            } else if (!ignoreLogin) {
                // edit login validation
                
                bindingResult.pushNestedPath("loginBackingBean");

                //When both password fields are left blank, this is a special case which means it
                // won't change the password, so here we don't want to do validation. 
                if (StringUtils.isNotBlank(accountGeneral.getLoginBackingBean().getPassword1()) 
                        || StringUtils.isNotBlank(accountGeneral.getLoginBackingBean().getPassword2())) {
                    passwordValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
                }
                
                usernameValidator.validate(accountGeneral.getLoginBackingBean(), bindingResult);
                bindingResult.popNestedPath();
                
                /* Check to see if the user is trying to modify the default user */
                checkEditingDefaultUser(accountGeneral.getLoginBackingBean().getUsername());
            }
            
            if (!bindingResult.hasErrors()) {
                
                transactionTemplate.execute(new TransactionCallback<Object>() {
                    
                    @Override
                    public Object doInTransaction(TransactionStatus status) {
                        /* see IMPORTANT NOTE comment in createAccount method, same rules apply here */
                        accountService.updateAccount(updatableAccount, accountId, userContext.getYukonUser());
                        operatorAccountService.updateAccount(accountId, accountGeneral.getOperatorGeneralUiExtras());
                        if (!ignoreLogin) {
                            if (LoginModeEnum.valueOf(loginMode) == LoginModeEnum.CREATE) {
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
                            int userId = SessionUtil.getParentLoginUserId(session, userContext.getYukonUser().getUserID());
                            EventUtils.logSTARSEvent(userId, EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                                     YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, 
                                                     accountInfoFragment.getAccountId());
                        }
                        return null;
                    }
                });
            }
            
        } catch (AccountNumberUnavailableException e) {
            bindingResult.rejectValue("accountDto.accountNumber", 
                    baseKey + "accountGeneral.accountDto.accountNumber.accountNumberUnavailable");
        } finally {
            setupAccountModel(accountInfoFragment, modelMap, userContext, ecResidentialUserGroups, residentialUser);

            if (bindingResult.hasErrors()) {
                
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                modelMap.addAttribute("mode", PageEditMode.EDIT);
                
                return "operator/account/account.jsp";
            } 
        }
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "account.accountUpdated"));
        return "redirect:view";
    }
    
    // DELETE LOGIN
    @RequestMapping(value="deleteLogin", method=RequestMethod.POST)
    public String deleteLogin(String loginMode, ModelMap model, YukonUserContext userContext, FlashScope flashScope,
            AccountInfoFragment accountInfoFragment) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        LiteYukonUser custYukonUser = customerAccountDao.getYukonUserByAccountId(accountInfoFragment.getAccountId());

        LoginModeEnum loginModeEnum = LoginModeEnum.valueOf(loginMode);
        if (LoginModeEnum.EDIT.equals(loginModeEnum)) {
            userDao.deleteUser(custYukonUser.getUserID());
            usersEventLogService.userDeleted(custYukonUser.getUsername(), userContext.getYukonUser());
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "account.userDeleted"));
        
        model.addAttribute("accountId", accountInfoFragment.getAccountId());
        return "redirect:view";
    }
    
    // DELETE ACCOUNT
    @RequestMapping(value="deleteAccount", method=RequestMethod.POST)
    public String deleteAccount(int accountId, YukonUserContext userContext, FlashScope flashScope)
            throws ServletRequestBindingException {
        
        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        accountEventLogService.accountDeletionAttempted(userContext.getYukonUser(), customerAccount.getAccountNumber(), 
                EventSource.OPERATOR);
        accountService.deleteAccount(accountId, userContext.getYukonUser());
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "account.accountDeleted"));
        return "redirect:/stars/operator/inventory/home";
    }
    
    // ACCOUNT LOG
    @RequestMapping("accountLog")
    public String accountLog(ModelMap model, AccountInfoFragment accountInfoFragment) {
        
        List<EventAccount> accountEvents = EventAccount.retrieveEventAccounts(accountInfoFragment.getAccountId());
        model.addAttribute("accountEvents",accountEvents);
        model.addAttribute("accountId", accountInfoFragment.getAccountId());
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, model);
        return "operator/account/accountLog.jsp";
    }
    
    // ACCOUNT GENERAL WRAPPER
    public static class AccountGeneral {
        
        private AccountDto accountDto = new AccountDto();
        private OperatorGeneralUiExtras operatorGeneralUiExtras = new OperatorGeneralUiExtras();
        private Login loginBackingBean = new Login();
        
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
        public void setLoginBackingBean(Login loginBackingBean) {
            this.loginBackingBean = loginBackingBean;
        }
        public Login getLoginBackingBean() {
            return loginBackingBean;
        }
        
    }
    
    private AccountImportResult initAccountImportResult(LiteYukonUser user, BulkFileUpload accountFileUpload, 
            BulkFileUpload hardwareFileUpload, String email, boolean prescan) {
        
        AccountImportResult result = new AccountImportResult();
        result.setCurrentUser(user);
        result.setAccountFileUpload(accountFileUpload);
        result.setHardwareFileUpload(hardwareFileUpload);
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        result.setEnergyCompany(energyCompany);
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
        EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        List<LiteUserGroup> ecResidentialUserGroups = ecMappingDao.getResidentialUserGroups(energyCompany.getId());
        boolean showLoginSection = ecSettingDao.getBoolean(EnergyCompanySettingType.AUTO_CREATE_LOGIN_FOR_ACCOUNT, 
                energyCompany.getId());
        
        List<Substation> substations = substationDao.getAllSubstationsByEnergyCompanyId(energyCompany.getId());
        modelMap.addAttribute("substations", substations);

        modelMap.addAttribute("loginMode", LoginModeEnum.CREATE);
        boolean supportsPasswordSet = 
                authenticationService.supportsPasswordSet(authenticationService.getDefaultAuthenticationCategory());
        modelMap.addAttribute("supportsPasswordSet", supportsPasswordSet);
        modelMap.addAttribute("energyCompanyId", energyCompany.getId());
        modelMap.addAttribute("showLoginSection", showLoginSection);
        modelMap.addAttribute("ecResidentialUserGroups", ecResidentialUserGroups);
        modelMap.addAttribute("mode", PageEditMode.CREATE);
    }
    
    private void setupAccountModel(AccountInfoFragment accountInfoFragment, ModelMap modelMap, 
            YukonUserContext userContext, List<LiteUserGroup> ecResidentialUserGroups, LiteYukonUser residentialUser) {
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        // substations
        List<Substation> substations = 
                substationDao.getAllSubstationsByEnergyCompanyId(accountInfoFragment.getEnergyCompanyId());
        modelMap.addAttribute("substations", substations);
        
        modelMap.addAttribute("energyCompanyId", accountInfoFragment.getEnergyCompanyId());
        
        modelMap.addAttribute("showLoginSection", hasEditLoginPrivileges(userContext.getYukonUser()));

        modelMap.addAttribute("supportsPasswordSet",
            authenticationService.supportsPasswordSet(authenticationService.getDefaultAuthenticationCategory()));

        modelMap.addAttribute("ecResidentialUserGroups", ecResidentialUserGroups);
        if (residentialUser.getUserID() == UserUtils.USER_NONE_ID) {
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
        LiteYukonUser defaultUserCheck = userDao.findUserByUsername(username);
        
        if (defaultUserCheck != null && defaultUserCheck.getUserID() == UserUtils.USER_NONE_ID) {
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
    private boolean didLoginChange(LiteYukonUser residentialUser, Login loginBackingBean, 
            String originalLoginUserGroupName) {
        
        String previousUsername = residentialUser.getUsername();
        
        boolean didNotChange = previousUsername.equals(loginBackingBean.getUsername())
            && StringUtils.isBlank(loginBackingBean.getPassword1()) 
            && StringUtils.isBlank(loginBackingBean.getPassword2())
            && StringUtils.equals(originalLoginUserGroupName, loginBackingBean.getUserGroupName())
            && residentialUser.getLoginStatus() == loginBackingBean.getLoginStatus();
        
        return !didNotChange;
    }
    
    @Resource(name="accountImportResultsCache")
    public void setRecentResultsCache(RecentResultsCache<AccountImportResult> recentResultsCache) {
        this.recentResultsCache = recentResultsCache;
    }
}