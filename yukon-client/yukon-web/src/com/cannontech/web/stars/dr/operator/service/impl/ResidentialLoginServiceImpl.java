package com.cannontech.web.stars.dr.operator.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import com.cannontech.core.authentication.model.AuthType;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.LoginBackingBean;
import com.cannontech.web.stars.dr.operator.service.ResidentialLoginService;

public class ResidentialLoginServiceImpl implements ResidentialLoginService{
    
    @Autowired private AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private TransactionOperations transactionTemplate;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;

    @Override
    public Integer createResidentialLogin(final LoginBackingBean loginBackingBean, final LiteYukonUser user, final int accountId, final int energyCompanyId) {

        Integer newUserId = transactionTemplate.execute(new TransactionCallback<Integer>() {
            
            public Integer doInTransaction(TransactionStatus status) {
                checkSuppliedResidentialUserGroup(energyCompanyId, loginBackingBean);
                LiteUserGroup residentialUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
                
                // Build up the user for creation
                LiteYukonUser newUser = new LiteYukonUser();
                newUser.setUsername(loginBackingBean.getUsername());
                if (residentialUserGroup != null) {
                    newUser.setUserGroupId(residentialUserGroup.getUserGroupId());
                }
                
                if (loginBackingBean.isLoginEnabled()) {
                    newUser.setLoginStatus(LoginStatusEnum.ENABLED);
                } else {
                    newUser.setLoginStatus(LoginStatusEnum.DISABLED);
                }

                // Get the default authType
                AuthType authType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE,
                                                                         AuthType.class, user);
                String password = loginBackingBean.getPassword1();
                // This is problematic.  It should be fixed with YUK-10019.
                if (password == null) {
                    authType = AuthType.NONE;
                }
                newUser.setAuthType(authType);
                yukonUserDao.save(newUser);

                // We need to use the AuthenticationService so the password gets encoded properly.
                if (authenticationService.supportsPasswordSet(authType)) {
                    authenticationService.setPassword(newUser, loginBackingBean.getPassword1());
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
    public void updateResidentialLogin(final LoginBackingBean loginBackingBean, final YukonUserContext userContext, 
                                        final LiteYukonUser residentialUser, final int energyCompanyId) {

        transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {

                // Update the user group for the user
                checkSuppliedResidentialUserGroup(energyCompanyId, loginBackingBean);
                LiteUserGroup newUserGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
                if (newUserGroup != null) {
                    residentialUser.setUserGroupId(newUserGroup.getUserGroupId());
                    yukonUserDao.updateUserGroupId(residentialUser.getUserID(), newUserGroup.getUserGroupId());
                } else {
                    yukonUserDao.updateUserGroupId(residentialUser.getUserID(), null);
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
    public void updateResidentialPassword(final LoginBackingBean loginBackingBean,
                                        final YukonUserContext userContext, 
                                        final LiteYukonUser residentialUser) {
        transactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
                if (!StringUtils.isBlank(loginBackingBean.getPassword1()) && !StringUtils.isBlank(loginBackingBean.getPassword2())) {
                    
                    // Security checks for password change.
                    rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD, userContext.getYukonUser());
                    AuthType currentAuthType = authenticationService.getDefaultAuthType(residentialUser);
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
    private void checkSuppliedResidentialUserGroup(final int energyCompanyId, final LoginBackingBean loginBackingBean) {
        List<LiteUserGroup> ecResidentialUserGroups = ecMappingDao.getResidentialUserGroups(energyCompanyId);

        LiteUserGroup residentialLoginGroup = userGroupDao.findLiteUserGroupByUserGroupName(loginBackingBean.getUserGroupName());
        if (residentialLoginGroup != null && !ecResidentialUserGroups.contains(residentialLoginGroup)) {
            throw new IllegalArgumentException();
        }
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
}