package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.service.ResidentialLoginService;
import com.google.common.collect.Lists;

public class ResidentialLoginServiceImpl implements ResidentialLoginService{
    
    private AuthenticationService authenticationService;
    private ContactDao contactDao;
    private RolePropertyDao rolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private TransactionOperations transactionTemplate;
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private YukonGroupService yukonGroupService;

    @Override
    public Integer createResidentialLogin(final LoginBackingBean loginBackingBean, final LiteYukonUser user, final int accountId, final int energyCompanyId) {

        Integer newUserId = transactionTemplate.execute(new TransactionCallback<Integer>() {
            
            public Integer doInTransaction(TransactionStatus status) {
                // Build up the groups needed to create an account
                List<LiteYukonGroup> groups = Lists.newArrayList();
                LiteYukonGroup defaultYukonGroup = yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
                groups.add(defaultYukonGroup);
                
                checkSuppliedResidentialLoginGroup(energyCompanyId, loginBackingBean);
                LiteYukonGroup residentialLoginGroup = yukonGroupDao.getLiteYukonGroupByName(loginBackingBean.getCustomerLoginGroupName());
                groups.add(residentialLoginGroup);
                
                // Build up the user for creation
                LiteYukonUser newUser = new LiteYukonUser();
                newUser.setUsername(loginBackingBean.getUsername());
                
                if (loginBackingBean.isLoginEnabled()) {
                    newUser.setLoginStatus(LoginStatusEnum.ENABLED);
                } else {
                    newUser.setLoginStatus(LoginStatusEnum.DISABLED);
                }
                
                // Get the default authType
                AuthType authType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, user);
                newUser.setAuthType(authType);
                
                String password = loginBackingBean.getPassword1();
                if (StringUtils.isBlank(password)) {
                    newUser.setAuthType(AuthType.NONE);
                }
                yukonUserDao.addLiteYukonUserWithPassword(newUser, password, groups);
                
                // Update primaryContact to new loginId
                LiteContact primaryContact = contactDao.getPrimaryContactForAccount(accountId);
                int newUserId = yukonUserDao.findUserByUsername(newUser.getUsername()).getUserID();
                primaryContact.setLoginID(newUserId);
                contactDao.saveContact(primaryContact);
                
                return newUserId;
            }
        });
        return newUserId;
    }
    
    @Override
    public void updateResidentialLogin(final LoginBackingBean loginBackingBean,
                                        final YukonUserContext userContext, 
                                        final LiteYukonUser residentialUser,
                                        final int energyCompanyId) {

        transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
    
                checkSuppliedResidentialLoginGroup(energyCompanyId, loginBackingBean);
                
                updateResidentialCustomerGroup(loginBackingBean.getCustomerLoginGroupName(), residentialUser);
    
                updateLoginStatus(loginBackingBean, residentialUser);
                
                if (!loginBackingBean.getUsername().equals(residentialUser.getUsername())) {
                    // Security check for username change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, userContext.getYukonUser());
                    yukonUserDao.changeUsername(userContext.getYukonUser(), residentialUser.getUserID(), loginBackingBean.getUsername());
                }
        
                if (!StringUtils.isBlank(loginBackingBean.getPassword1()) 
                        && !StringUtils.isBlank(loginBackingBean.getPassword2())) {
                    
                    // Security checks for password change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
                    boolean passwordSetSupported = authenticationService.supportsPasswordSet(residentialUser.getAuthType());
                    if (!passwordSetSupported) {
                        throw new IllegalArgumentException("You cannot set the password on this style of account.");
                    }
    
                    authenticationService.setPassword(residentialUser, loginBackingBean.getPassword1());
                }
                
                return null;
        
            }
    
        });
    }
    
    // HELPERS
    
    /**
     * Verifies that the login group of the backing bean is one of the residential login groups
     * of the the energy company.  If not, IllegalArgumentException is thrown.
     * @param energyCompanyId
     * @param loginBackingBean
     */
    private void checkSuppliedResidentialLoginGroup(final int energyCompanyId, final LoginBackingBean loginBackingBean) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        List<LiteYukonGroup> ecResidentialGroups = Lists.newArrayList(energyCompany.getResidentialCustomerGroups());
        LiteYukonGroup residentialLoginGroup = yukonGroupDao.getLiteYukonGroupByName(loginBackingBean.getCustomerLoginGroupName());
        if (!ecResidentialGroups.contains(residentialLoginGroup)) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * This method removes the old residential group, adds a new residential group, 
     * and sends a db change message.
     */
     private void updateResidentialCustomerGroup(String groupName, LiteYukonUser residentialUser) {
         LiteYukonGroup newUserGroup = yukonGroupDao.getLiteYukonGroupByName(groupName);
         yukonGroupService.addUserToGroup(newUserGroup.getGroupID(), residentialUser.getUserID());
    }
    
    private void updateLoginStatus(LoginBackingBean loginBackingBean, LiteYukonUser residentialUser) {
        
        LoginStatusEnum loginStatus = null;
        if (loginBackingBean.isLoginEnabled()) {
            loginStatus = LoginStatusEnum.ENABLED;
        } else {
            loginStatus = LoginStatusEnum.DISABLED;
        }
        yukonUserDao.setUserStatus(residentialUser, loginStatus);
    }
    
    // DI Setters
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
    
    @Autowired
    public void setYukonGroupService(YukonGroupService yukonGroupService) {
        this.yukonGroupService = yukonGroupService;
    }
}