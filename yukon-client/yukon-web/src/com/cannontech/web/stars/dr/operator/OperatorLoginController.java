package com.cannontech.web.stars.dr.operator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.constants.YukonListEntryTypes;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.login.ChangeLoginMessage;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.ChangeLoginBackingBean;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty({YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD})
@RequestMapping(value = "/operator/login/*")
public class OperatorLoginController {

    public static final String LOGIN_CHANGE_MESSAGE_PARAM = "loginChangeMsg";
    public static final String LOGIN_CHANGE_SUCCESS_PARAM = "success";

    private AuthenticationService authenticationService;
    private ContactDao contactDao;
    private RolePropertyDao rolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    
    // CHANGE LOGIN
    @RequestMapping
    public String changeLogin(int accountId, 
    		                  int energyCompanyId, 
    		                  HttpServletRequest request, 
    		                  ModelMap modelMap,
    		                  YukonUserContext userContext,
    		                  AccountInfoFragment accountInfoFragment) throws ServletRequestBindingException {
        
        // Build up residential login group list.
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        List<String> ecResidentialGroupNames = getECResidentialGroupNames(ecResidentialGroups);
        modelMap.addAttribute("ecResidentialGroupNames", ecResidentialGroupNames);
        
        LiteYukonUser residentialUser = getYukonUserByAccountId(accountId);
        
        // Build up backing bean
        ChangeLoginBackingBean changeLoginBackingBean = new ChangeLoginBackingBean();
        String userResidentialGroupName = getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        changeLoginBackingBean.setCustomerLoginGroupName(userResidentialGroupName);
        changeLoginBackingBean.setUsername(residentialUser.getUsername());
        changeLoginBackingBean.setLoginEnabled(residentialUser.getStatus());
        modelMap.addAttribute("changeLoginBackingBean", changeLoginBackingBean);
        
        // Set supported variables
        modelMap.addAttribute("supportsPasswordSet", authenticationService.supportsPasswordSet(residentialUser.getAuthType()));
        modelMap.addAttribute("supportsPasswordChange", authenticationService.supportsPasswordChange(residentialUser.getAuthType()));
        
// TODO not needed when using flashScope?
//        // Setting help messages
//        String usernameChangeMessageStr = ServletRequestUtils.getStringParameter(request, "usernameChangeMessage");
//        if (usernameChangeMessageStr != null) {
//            ChangeLoginMessage usernameChangeMessage = ChangeLoginMessage.valueOf(usernameChangeMessageStr);
//            modelMap.addAttribute("usernameChangeMessage", usernameChangeMessage);
//        }
//        
//        String passwordChangeMessageStr = ServletRequestUtils.getStringParameter(request, "passwordChangeMessage");
//        if (passwordChangeMessageStr != null) {
//            ChangeLoginMessage passwordChangeMessage = ChangeLoginMessage.valueOf(passwordChangeMessageStr);
//            modelMap.addAttribute("passwordChangeMessage", passwordChangeMessage);
//        }
        
        
        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "operator/login/login.jsp";
    }
    
    // UPDATE LOGIN
    @RequestMapping
    public String updateLogin(int accountId, 
    		                  int energyCompanyId, 
    		                  @ModelAttribute ChangeLoginBackingBean changeLoginBackingBean, 
                              ModelMap modelMap, 
                              YukonUserContext userContext, 
                              HttpSession session,
                              FlashScope flashScope,
                              AccountInfoFragment accountInfoFragment) {
    	
        // Check to see what the user can modify
        rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        LiteYukonUser residentialUser = getYukonUserByAccountId(accountId);
        
        if (residentialUser.getLiteID() == UserUtils.USER_DEFAULT_ID){
            
            // Build up the groups needed to create an account
            List<LiteYukonGroup> groups = Lists.newArrayList();
            LiteYukonGroup defaultYukonGroup = yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
            groups.add(defaultYukonGroup);
            LiteYukonGroup residentialLoginGroup = yukonGroupDao.getLiteYukonGroupByName(changeLoginBackingBean.getCustomerLoginGroupName());
            groups.add(residentialLoginGroup);

            ChangeLoginMessage validatePasswordChange = validatePasswordChange(changeLoginBackingBean,
                                                                               userContext, 
                                                                               residentialUser);
            if (ChangeLoginMessage.LOGIN_PASSWORD_CHANGED.equals(validatePasswordChange)) {

                // Build up the user for creation
                LiteYukonUser newUser = new LiteYukonUser();
                newUser.setUsername(changeLoginBackingBean.getUsername());
                
                if (changeLoginBackingBean.isLoginEnabled()) {
                    newUser.setStatus(UserUtils.STATUS_ENABLED);
                } else {
                    newUser.setStatus(UserUtils.STATUS_DISABLED);
                }

                // Get the default authType
                AuthType authType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, userContext.getYukonUser());
                newUser.setAuthType(authType);
                
                String password = changeLoginBackingBean.getPassword1();
                if (StringUtils.isBlank(password)) {
                    newUser.setAuthType(AuthType.NONE);
                }
                yukonUserDao.addLiteYukonUserWithPassword(newUser, password, energyCompanyId, groups);
                
                
                // Update primaryContact to new loginId
                LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
                int newUserId = yukonUserDao.getLiteYukonUser(newUser.getUsername()).getUserID();
                primaryContact.setLoginID(newUserId);
                contactDao.saveContact(primaryContact);
                
                // Added Event Log Message
                EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, accountId, session);
            
                // flashScope message
                MessageSourceResolvable validatePasswordChangeMessage = new YukonMessageSourceResolvable(validatePasswordChange.getFormatKey());
                flashScope.setMessages(Collections.singletonList(validatePasswordChangeMessage), validatePasswordChange.getFlashScopeMessageType());
            }
            
        } else {

            updateResidentialCustomerGroup(energyCompanyId, changeLoginBackingBean, residentialUser);
            updateLoginStatus(changeLoginBackingBean, residentialUser);
            
            // Handle username change
            ChangeLoginMessage usernameChangeMessage = validateUsernameChange(changeLoginBackingBean,
                                                                              userContext,
                                                                              residentialUser);
            if (ChangeLoginMessage.LOGIN_USERNAME_CHANGED.equals(usernameChangeMessage)) {
                yukonUserDao.changeUsername(residentialUser.getUserID(), changeLoginBackingBean.getUsername());

                // Added Event Log Message
                EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, accountId, session);
            }
    
            // Handle password change
            ChangeLoginMessage passwordChangeMessage = validatePasswordChange(changeLoginBackingBean,
                                                                              userContext,
                                                                              residentialUser);
            if (ChangeLoginMessage.LOGIN_PASSWORD_CHANGED.equals(passwordChangeMessage)) {
                authenticationService.setPassword(residentialUser, changeLoginBackingBean.getPassword1());
                
                // Added Event Log Message
                EventUtils.logSTARSEvent(userContext.getYukonUser().getUserID(), EventUtils.EVENT_CATEGORY_ACCOUNT, YukonListEntryTypes.EVENT_ACTION_CUST_ACCT_UPDATED, accountId, session);
            }
            
            // flashScope messages
            MessageSourceResolvable usernameMessage = new YukonMessageSourceResolvable(usernameChangeMessage.getFormatKey());
            MessageSourceResolvable passwordMessage = new YukonMessageSourceResolvable(passwordChangeMessage.getFormatKey());
            if (usernameChangeMessage.getFlashScopeMessageType() == passwordChangeMessage.getFlashScopeMessageType()) {
            	List<MessageSourceResolvable> messages = Lists.newArrayListWithCapacity(2);
            	messages.add(usernameMessage);
            	messages.add(passwordMessage);
            	flashScope.setMessages(messages, usernameChangeMessage.getFlashScopeMessageType());
            } else {
            	flashScope.setMessages(Collections.singletonList(usernameMessage), usernameChangeMessage.getFlashScopeMessageType());
            	flashScope.setMessages(Collections.singletonList(passwordMessage), passwordChangeMessage.getFlashScopeMessageType());
            }
        }

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:changeLogin";
    }


    // GENERATE PASSWORD
    @RequestMapping
    public String generatePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        response.setContentType("application/json");
        JSONObject object = new JSONObject();
        
        // Generate password
        String generatedPassword = authenticationService.generatePassword(6);
        object.put("generatedPassword", generatedPassword);
        
        // Write out javascript response
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
        return generatedPassword;
    }
    
    // DELETE LOGIN
    @RequestMapping
    public String deleteLogin(int accountId, 
    		                  int energyCompanyId,
    		                  ModelMap modelMap, 
    		                  YukonUserContext userContext,
    		                  AccountInfoFragment accountInfoFragment) {
        
    	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_ALLOW_ACCOUNT_EDITING, userContext.getYukonUser());
        
        LiteYukonUser custYukonUser = getYukonUserByAccountId(accountId);
        yukonUserDao.deleteUser(custYukonUser.getUserID());

        AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
        return "redirect:changeLogin";
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
     * This method removes the old residential group, adds a new residential group, and sends a db change message.
     */
    private void updateResidentialCustomerGroup(int energyCompanyId,
                                                  ChangeLoginBackingBean changeLoginBackingBean,
                                                  LiteYukonUser residentialUser) {
        
    	// Get all the the residential groups
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        String previousUserResidentialGroupName = getResidentialGroupNameForUser(residentialUser.getUserID(), ecResidentialGroups);
        
        if (!previousUserResidentialGroupName.equals(changeLoginBackingBean.getCustomerLoginGroupName())) {
            // Remove the old login group
            LiteYukonGroup previousResidentialLoginGroup = yukonGroupDao.getLiteYukonGroupByName(previousUserResidentialGroupName);
            yukonUserDao.removeUserFromGroup(residentialUser, previousResidentialLoginGroup);
            
            // Add the new login group
            LiteYukonGroup newResidentialLoginGroup = yukonGroupDao.getLiteYukonGroupByName(changeLoginBackingBean.getCustomerLoginGroupName());
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
     * This method handles all the validation for a password change.
     *
     * @return - ChangeLoginMessage.  It will return ChangeLoginMessage.LOGIN_PASSWORD_CHANGED if all validation requirements were meet.
     */
    private ChangeLoginMessage validatePasswordChange(ChangeLoginBackingBean changeLoginBackingBean,
                                                      YukonUserContext userContext,
                                                      LiteYukonUser residentialUser) {
        
    	boolean allowsPasswordChange = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
        boolean passwordChangeSupported = authenticationService.supportsPasswordChange(residentialUser.getAuthType());
        
        ChangeLoginMessage passwordChangeMessage = null;
        if (allowsPasswordChange) {
            if (StringUtils.isBlank(changeLoginBackingBean.getPassword1()) &&
                StringUtils.isBlank(changeLoginBackingBean.getPassword2())) {
                passwordChangeMessage = ChangeLoginMessage.NO_PASSWORD_CHANGE;
            } else if (!passwordChangeSupported) {
                passwordChangeMessage = ChangeLoginMessage.PASSWORD_CHANGE_NOT_SUPPORTED;
            } else if (!changeLoginBackingBean.getPassword1().equals(changeLoginBackingBean.getPassword2())) {
                passwordChangeMessage = ChangeLoginMessage.NO_PASSWORDMATCH;
            } else {
                passwordChangeMessage = ChangeLoginMessage.LOGIN_PASSWORD_CHANGED;
            }
        }
        return passwordChangeMessage;
    }
    
    /**
     * This method handles all the validation for a username change.
     *
     * @return - ChangeLoginMessage.  It will return ChangeLoginMessage.LOGIN_USERNAME_CHANGED if all validation requirements were meet.
     */
    private ChangeLoginMessage validateUsernameChange(ChangeLoginBackingBean changeLoginBackingBean,
                                                      YukonUserContext userContext,
                                                      LiteYukonUser residentialUser) {
        
    	// Handle username change
        boolean allowsUsernameChange = rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, userContext.getYukonUser());
        
        ChangeLoginMessage usernameChangeMessage = null;
        if (allowsUsernameChange) {
            if (changeLoginBackingBean.getUsername().equals(residentialUser.getUsername())) {
                usernameChangeMessage = ChangeLoginMessage.NO_USERNAME_CHANGE;
            } else {
                usernameChangeMessage = ChangeLoginMessage.LOGIN_USERNAME_CHANGED;
            }
        }
        return usernameChangeMessage;
    }
    
    /**
     * Returns the first residential login group that the user is in.
     * 
     */
    private String getResidentialGroupNameForUser(int userId, List<LiteYukonGroup> ecResidentialGroups) {

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

    /**
     * Returns all the group names for a given list of liteYukonGroup objects
     * 
     */
    private List<String> getECResidentialGroupNames(List<LiteYukonGroup> ecResidentialGroups) {
        
    	ArrayList<String> results = Lists.newArrayList();

        for (LiteYukonGroup yukonGroup : ecResidentialGroups) {
            results.add(yukonGroup.getGroupName());
        }
        
        if (ecResidentialGroups.size() < 1){
            results.add("(none)");
        }
        
        return results;
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
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
