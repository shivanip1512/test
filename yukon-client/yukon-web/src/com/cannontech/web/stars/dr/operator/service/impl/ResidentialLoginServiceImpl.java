package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.login.model.Login;
import com.cannontech.web.stars.dr.operator.service.ResidentialLoginService;

public class ResidentialLoginServiceImpl implements ResidentialLoginService{
    @Autowired private AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private TransactionOperations transactionTemplate;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UsersEventLogService usersEventLogService;

    @Override
    public Integer createResidentialLogin(final Login loginBackingBean, final LiteYukonUser user,
            final int accountId, final int energyCompanyId) {
        Integer newUserId = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                checkSuppliedResidentialUserGroup(energyCompanyId, loginBackingBean);
                LiteUserGroup residentialUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
                
                // Build up the user for creation
                LoginStatusEnum loginStatus = (loginBackingBean.isLoginEnabled() ? 
                						LoginStatusEnum.ENABLED : LoginStatusEnum.DISABLED);

                // This is not preferred, but until we correctly rewrite a way to "create" a YukonUser this is how it will be.
                LiteYukonUser newUser = new LiteYukonUser(LiteYukonUser.CREATE_NEW_USER_ID, 
                						loginBackingBean.getUsername(), 
                						loginStatus);

                if (residentialUserGroup != null) {
                    newUser.setUserGroupId(residentialUserGroup.getUserGroupId());
                }
                String energyCompanyName = ecDao.getEnergyCompany(energyCompanyId).getName();
                yukonUserDao.save(newUser);
                usersEventLogService.userCreated(newUser.getUsername(), residentialUserGroup.getUserGroupName(), energyCompanyName, newUser.getLoginStatus() , user);
                if (residentialUserGroup != null) {
                    usersEventLogService.userAdded(newUser.getUsername(), residentialUserGroup.getUserGroupName(), user);
                }
                
                // We need to use the AuthenticationService so the password gets encoded properly.
                AuthenticationCategory authenticationCategory = authenticationService.getDefaultAuthenticationCategory();
                String password = loginBackingBean.getPassword1();
                if (authenticationService.supportsPasswordSet(authenticationCategory)) {
                    if (StringUtils.isBlank(password)) {
                        throw new RuntimeException("password required for authentication category " + authenticationCategory);
                    }
                    authenticationService.setPassword(newUser, loginBackingBean.getPassword1());
                } else {
                    authenticationService.setAuthenticationCategory(newUser, authenticationCategory);
                }

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
    public void updateResidentialLogin(final Login loginBackingBean, final YukonUserContext userContext, 
                                        final LiteYukonUser residentialUser, final int energyCompanyId) {

        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                // Update the user group for the user
                checkSuppliedResidentialUserGroup(energyCompanyId, loginBackingBean);
                LiteUserGroup newUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
                if (newUserGroup != null) {
                    residentialUser.setUserGroupId(newUserGroup.getUserGroupId());
                    yukonUserDao.updateUserGroupId(residentialUser.getUserID(), newUserGroup.getUserGroupId());
                    usersEventLogService.userAdded(residentialUser.getUsername(), newUserGroup.getUserGroupName(), userContext.getYukonUser());
                } else {
                    yukonUserDao.updateUserGroupId(residentialUser.getUserID(), null);
                    usersEventLogService.userAdded(residentialUser.getUsername(), null, userContext.getYukonUser());
                }

                updateLoginStatus(loginBackingBean, residentialUser);
                
                if (!loginBackingBean.getUsername().equals(residentialUser.getUsername())) {
                    // Security check for username change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME, userContext.getYukonUser());
                    yukonUserDao.changeUsername(userContext.getYukonUser(), residentialUser.getUserID(), loginBackingBean.getUsername());
                }
                return null;
            }
        });
    }
    
    @Override
    public void updateResidentialPassword(final Login loginBackingBean,
                                        final YukonUserContext userContext, 
                                        final LiteYukonUser residentialUser) {
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                if (!StringUtils.isBlank(loginBackingBean.getPassword1()) && !StringUtils.isBlank(loginBackingBean.getPassword2())) {
                    
                    // Security checks for password change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
                    AuthType currentAuthType = authenticationService.getDefaultAuthType();
                    boolean passwordSetSupported = authenticationService.supportsPasswordSet(currentAuthType);
                    if (!passwordSetSupported) {
                        throw new IllegalArgumentException("You cannot set the password on this style of account.");
                    }
                    
                    /* Update the password */
                    authenticationService.setPassword(residentialUser, loginBackingBean.getPassword1());
                }
                return null;
            }
        });
    }
    
    // HELPERS
    
    /**
     * Verifies that the user group of the backing bean is one of the residential user groups
     * of the the energy company.
     * 
     * @throws IllegalArgumentException - the user group in the backing bean is not a apart of the energy company.
     */
    private void checkSuppliedResidentialUserGroup(final int energyCompanyId, final Login loginBackingBean) {
        List<LiteUserGroup> ecResidentialUserGroups = ecMappingDao.getResidentialUserGroups(energyCompanyId);

        LiteUserGroup residentialLoginGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
        if (residentialLoginGroup != null && !ecResidentialUserGroups.contains(residentialLoginGroup)) {
            throw new IllegalArgumentException();
        }
    }
    
    private void updateLoginStatus(Login loginBackingBean, LiteYukonUser residentialUser) {
        
        LoginStatusEnum loginStatus = null;
        if (loginBackingBean.isLoginEnabled()) {
            loginStatus = LoginStatusEnum.ENABLED;
        } else {
            loginStatus = LoginStatusEnum.DISABLED;
        }
        yukonUserDao.setUserStatus(residentialUser, loginStatus);
    }
}
