package com.cannontech.web.stars.dr.operator;

import java.io.IOException;
import java.util.List;

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

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.ChangeLoginBackingBean;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidator;
import com.cannontech.web.stars.dr.operator.validator.ChangeLoginValidatorFactory;
import com.cannontech.web.util.TextView;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty({YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, 
                    YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD})
@RequestMapping(value = "/operator/login/*")
public class OperatorChangeLoginController {

    private AuthenticationService authenticationService;
    private ChangeLoginValidatorFactory changeLoginValidatorFactory;
    private ContactDao contactDao;
    private RolePropertyDao rolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private SystemEventLogService systemEventLogService;
    private TransactionOperations transactionTemplate;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;

    
    // CHANGE LOGIN
    @RequestMapping
    public String changeLogin(HttpServletRequest request, 
                               ModelMap modelMap,
                               YukonUserContext userContext,
                               AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        LiteYukonUser residentialUser = getYukonUserByAccountId(accountInfoFragment.getAccountId());
        List<LiteYukonGroup> ecResidentialGroups = 
            setupLoginModelMap(accountInfoFragment.getEnergyCompanyId(),
                               residentialUser,
                               modelMap);
        
        // Build up backing bean
        ChangeLoginBackingBean changeLoginBackingBean = new ChangeLoginBackingBean();
        String userResidentialGroupName = 
            getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        changeLoginBackingBean.setCustomerLoginGroupName(userResidentialGroupName);

        // Checks to see if the account has a login
        boolean allowAccountEditing = 
            rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                          userContext.getYukonUser());
        if (residentialUser.getUserID() == UserUtils.USER_DEFAULT_ID) {
            modelMap.addAttribute("loginMode", LoginModeEnum.CREATE);
            changeLoginBackingBean.setLoginEnabled(LoginStatusEnum.ENABLED);
            modelMap.addAttribute("mode", 
                                  allowAccountEditing ? PageEditMode.CREATE : PageEditMode.VIEW);
        } else {
            changeLoginBackingBean.setUsername(residentialUser.getUsername());
            changeLoginBackingBean.setLoginEnabled(residentialUser.getLoginStatus());
            modelMap.addAttribute("loginMode", LoginModeEnum.EDIT);
            modelMap.addAttribute("mode", 
                                  allowAccountEditing ? PageEditMode.EDIT : PageEditMode.VIEW);
        }

        modelMap.addAttribute("changeLoginBackingBean", changeLoginBackingBean);
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        
        // pageEditMode
		
        return "operator/changeLogin/changeLogin.jsp";
    }

    private List<LiteYukonGroup> setupLoginModelMap(int energyCompanyId, 
                                                     LiteYukonUser residentialUser,
                                                     ModelMap modelMap) {
        // Set supported variables
        modelMap.addAttribute("supportsPasswordSet", 
                              authenticationService.supportsPasswordSet(residentialUser.getAuthType()));
        
        // Build up residential login group list.
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = 
            Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        modelMap.addAttribute("ecResidentialGroups", ecResidentialGroups);
        return ecResidentialGroups;
    }
    
    // UPDATE LOGIN
    @RequestMapping
    public String updateLogin(@ModelAttribute("changeLoginBackingBean") ChangeLoginBackingBean changeLoginBackingBean, 
                               BindingResult bindingResult,
                               HttpServletRequest request, 
                               ModelMap modelMap, 
                               YukonUserContext userContext, 
                               HttpSession session,
                               FlashScope flashScope,
                               AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        LiteYukonUser residentialUser = getYukonUserByAccountId(accountInfoFragment.getAccountId());

        systemEventLogService.loginChangeAttemptedByOperator(userContext.getYukonUser(),
                                                             residentialUser.getUsername());

        String loginMode = ServletRequestUtils.getStringParameter(request, "loginMode");
        modelMap.addAttribute("loginMode", loginMode);
        
        // Check to see what the user can modify
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                       userContext.getYukonUser());
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        // validate/update
        ChangeLoginValidator changeLoginValidator = 
            changeLoginValidatorFactory.getChangeLoginValidator(residentialUser);
        
        try {

            changeLoginValidator.validate(changeLoginBackingBean, bindingResult);

            if (!bindingResult.hasErrors()) {

                // Check to see if the user is trying to modify the default user
                LiteYukonUser defaultUserCheck = 
                    yukonUserDao.getLiteYukonUser(changeLoginBackingBean.getUsername());
                if (defaultUserCheck != null &&
                    defaultUserCheck.getUserID() == UserUtils.USER_DEFAULT_ID) {
                    throw new RuntimeException("You cannot edit the the default user.");
                }
                
                LoginModeEnum loginModeEnum = LoginModeEnum.valueOf(loginMode);
                if (LoginModeEnum.CREATE.equals(loginModeEnum)){

                    createResidentialLogin(changeLoginBackingBean, 
                                           userContext, 
                                           residentialUser, 
                                           accountInfoFragment);

                    // Added Event Log Message
                    EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), 
                                             EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                             YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, 
                                             accountInfoFragment.getAccountId(), session);

                    flashScope.setConfirm(
                                   new YukonMessageSourceResolvable(
                                           "yukon.web.modules.operator.changeLogin.loginCreated"));

                } else {

                    updateResidentialLogin(changeLoginBackingBean, modelMap, userContext, 
                                           residentialUser, accountInfoFragment);

                    // Added Event Log Message
                    EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), 
                                             EventUtils.EVENT_CATEGORY_ACCOUNT, 
                                             YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, 
                                             accountInfoFragment.getAccountId(), session);

                    flashScope.setConfirm(
                                   new YukonMessageSourceResolvable(
                                           "yukon.web.modules.operator.changeLogin.loginUpdated"));
                }

            }
        } finally {
            
            AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
            if (bindingResult.hasErrors()) {
                setupLoginModelMap(accountInfoFragment.getEnergyCompanyId(), 
                                   residentialUser, 
                                   modelMap);
                
                List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
				flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
                return "operator/changeLogin/changeLogin.jsp";
            } 
        }
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:changeLogin";
    }

    // GENERATE PASSWORD
    @RequestMapping
    public TextView generatePassword(HttpServletRequest request, 
                                      HttpServletResponse response) throws IOException {
        
        response.setContentType("text/plain");
        
        // Generate password
        String generatedPassword = RandomStringUtils.randomAlphanumeric(6);
        return new TextView(generatedPassword);
        
    }
    
    // DELETE LOGIN
    @RequestMapping
    public String deleteLogin(String loginMode,
                               ModelMap modelMap, 
                               YukonUserContext userContext,
                               FlashScope flashScope,
                               AccountInfoFragment accountInfoFragment) {

        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, 
                                       userContext.getYukonUser());
        
        LiteYukonUser custYukonUser = getYukonUserByAccountId(accountInfoFragment.getAccountId());

        LoginModeEnum loginModeEnum = LoginModeEnum.valueOf(loginMode);
        if (LoginModeEnum.EDIT.equals(loginModeEnum)) {
            yukonUserDao.deleteUser(custYukonUser.getUserID());
        }

        flashScope.setConfirm(
                       new YukonMessageSourceResolvable(
                               "yukon.web.modules.operator.changeLogin.loginDeleted"));
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:changeLogin";
    }
    
    /**
     * 
     * This method updates an existing residential login
     *
     */
    private void updateResidentialLogin(final ChangeLoginBackingBean changeLoginBackingBean,
                                          ModelMap modelMap, 
                                          final YukonUserContext userContext, 
                                          final LiteYukonUser residentialUser,
                                          final AccountInfoFragment accountInfoFragment) {


        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {

                checkSuppliedResidentialLoginGroup(accountInfoFragment.getEnergyCompanyId(),
                                                   changeLoginBackingBean);
                updateResidentialCustomerGroup(accountInfoFragment.getEnergyCompanyId(), 
                                               changeLoginBackingBean, 
                                               residentialUser);

                updateLoginStatus(changeLoginBackingBean, residentialUser);
                
                if (!changeLoginBackingBean.getUsername().equals(residentialUser.getUsername())) {
                    // Security check for username change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, 
                                                   userContext.getYukonUser());

                    yukonUserDao.changeUsername(userContext.getYukonUser(),
                                                residentialUser.getUserID(), 
                                                changeLoginBackingBean.getUsername());
                }
        
                if (!StringUtils.isBlank(changeLoginBackingBean.getPassword1()) &&
                    !StringUtils.isBlank(changeLoginBackingBean.getPassword2())) {
                    
                    // Security checks for password change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, 
                                                   userContext.getYukonUser());
                    boolean passwordSetSupported = 
                        authenticationService.supportsPasswordSet(residentialUser.getAuthType());
                    if (!passwordSetSupported) {
                        throw new IllegalArgumentException("You cannot set the password on this style of account.");
                    }

                    authenticationService.setPassword(residentialUser, 
                                                      changeLoginBackingBean.getPassword1());
                }
                
                return null;
        
            }

        });
    }

    /**
     * @param energyCompanyId
     * @param changeLoginBackingBean
     */
    private void checkSuppliedResidentialLoginGroup(final int energyCompanyId,
                                                      final ChangeLoginBackingBean changeLoginBackingBean) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = 
            Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        LiteYukonGroup residentialLoginGroup = 
            yukonGroupDao.getLiteYukonGroupByName(changeLoginBackingBean.getCustomerLoginGroupName());
        if (!ecResidentialGroups.contains(residentialLoginGroup)) {
            throw new IllegalArgumentException();
        }
    }

    
    /**
     *
     * This method creates a brand new residential login
     * 
     * @param accountId - the account we are modifying
     * @param energyCompanyId - the energy company
     * @param changeLoginBackingBean - the backing bean for the request
     * @param userContext - the userContext we are logged in with
     * @param residentialUser - the residential user we are modifying
     */
    private void createResidentialLogin(final ChangeLoginBackingBean changeLoginBackingBean,
                                          final YukonUserContext userContext, 
                                          final LiteYukonUser residentialUser,
                                          final AccountInfoFragment accountInfoFragment) {


        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {

                // Build up the groups needed to create an account
                List<LiteYukonGroup> groups = Lists.newArrayList();
                LiteYukonGroup defaultYukonGroup = 
                    yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
                groups.add(defaultYukonGroup);
                
                checkSuppliedResidentialLoginGroup(accountInfoFragment.getEnergyCompanyId(),
                                                   changeLoginBackingBean);
                LiteYukonGroup residentialLoginGroup = 
                    yukonGroupDao.getLiteYukonGroupByName(changeLoginBackingBean.getCustomerLoginGroupName());
                groups.add(residentialLoginGroup);
        
                // Build up the user for creation
                LiteYukonUser newUser = new LiteYukonUser();
                newUser.setUsername(changeLoginBackingBean.getUsername());

                if (changeLoginBackingBean.isLoginEnabled()) {
                    newUser.setLoginStatus(LoginStatusEnum.ENABLED);
                } else {
                    newUser.setLoginStatus(LoginStatusEnum.DISABLED);
                }

                // Get the default authType
                AuthType authType = 
                    rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, 
                                                         AuthType.class, 
                                                         userContext.getYukonUser());
                newUser.setAuthType(authType);
        
                String password = changeLoginBackingBean.getPassword1();
                if (StringUtils.isBlank(password)) {
                    newUser.setAuthType(AuthType.NONE);
                }
                yukonUserDao.addLiteYukonUserWithPassword(newUser, 
                                                          password, 
                                                          accountInfoFragment.getEnergyCompanyId(), 
                                                          groups);
        
                // Update primaryContact to new loginId
                LiteContact primaryContact = 
                    contactDao.getPrimaryContactForAccount(accountInfoFragment.getAccountId());
                int newUserId = yukonUserDao.getLiteYukonUser(newUser.getUsername()).getUserID();
                primaryContact.setLoginID(newUserId);
                contactDao.saveContact(primaryContact);
                
                return null;
        
            }
        });
    }

    private void updateLoginStatus(ChangeLoginBackingBean changeLoginBackingBean,
                                     LiteYukonUser residentialUser) {
        
        LoginStatusEnum loginStatus = null;
        if (changeLoginBackingBean.isLoginEnabled()) {
            loginStatus = LoginStatusEnum.ENABLED;
        } else {
            loginStatus = LoginStatusEnum.DISABLED;
        }
        yukonUserDao.setUserStatus(residentialUser, loginStatus);
    }

    /**
     * This method removes the old residential group, adds a new residential group, 
     * and sends a db change message.
     */
    private void updateResidentialCustomerGroup(int energyCompanyId,
                                                  ChangeLoginBackingBean changeLoginBackingBean,
                                                  LiteYukonUser residentialUser) {

        // Get all the the residential groups
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = 
            Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        String previousUserResidentialGroupName = 
            getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        
        if (!previousUserResidentialGroupName.equals(changeLoginBackingBean.getCustomerLoginGroupName())) {
            // Remove the old login group
            LiteYukonGroup previousResidentialLoginGroup = 
                yukonGroupDao.getLiteYukonGroupByName(previousUserResidentialGroupName);
            yukonUserDao.removeUserFromGroup(residentialUser, previousResidentialLoginGroup);
            
            // Add the new login group
            LiteYukonGroup newResidentialLoginGroup = 
                yukonGroupDao.getLiteYukonGroupByName(changeLoginBackingBean.getCustomerLoginGroupName());
            yukonUserDao.addUserToGroup(residentialUser, newResidentialLoginGroup);
        }
    }

    
    /**
     * Gets the login that is attached to the account
     * 
     */
    private LiteYukonUser getYukonUserByAccountId(int accountId) {
        LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
        LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(primaryContact.getLoginID());
        return liteYukonUser;
    }
    
    /**
     * Returns the first residential login group that the user is in.
     * 
     */
    private String getResidentialGroupNameForUser(int userId,
                                                   List<LiteYukonGroup> ecResidentialGroups) {

        List<LiteYukonGroup> groupsForUser = yukonGroupDao.getGroupsForUser(userId);
        for (LiteYukonGroup userLiteYukonGroup : groupsForUser) {
            for (LiteYukonGroup ecResidentialGroup : ecResidentialGroups){
                if (userLiteYukonGroup.equals(ecResidentialGroup)){
                    return ecResidentialGroup.getGroupName();
                }
            }
        }
        
        return null;
    }
    
    static private enum LoginModeEnum {
        CREATE,
        EDIT,
        VIEW;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Autowired
    public void setChangeLoginValidatorFactory(ChangeLoginValidatorFactory changeLoginValidatorFactory) {
        this.changeLoginValidatorFactory = changeLoginValidatorFactory;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

    @Autowired
    public void setSystemEventLogService(SystemEventLogService systemEventLogService) {
        this.systemEventLogService = systemEventLogService;
    }
    
    @Autowired
    public void setTransactionTemplate(TransactionOperations transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
}
