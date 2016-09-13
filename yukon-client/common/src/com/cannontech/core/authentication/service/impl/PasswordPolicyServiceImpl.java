package com.cannontech.core.authentication.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class PasswordPolicyServiceImpl implements PasswordPolicyService {
    
    @Autowired private AuthenticationService authService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    
    @Override
    public List<PolicyRule> getPolicyRules(LiteYukonUser user) {
        
        Validate.notNull(user);
        
        // There is not a password policy for this user.
        if (!rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)) {
            return getDefaultPasswordPolicy().getPolicyRules();
        }
        
        // Go through the available policies and see which ones are being use for this user. 
        List<PolicyRule> results = Lists.newArrayList();
        for (PolicyRule policyRule : PolicyRule.values()) {
            YukonRoleProperty policyRoleProperty = YukonRoleProperty.valueOf("POLICY_RULE_"+policyRule.name());
            boolean isPolicyUsed = rolePropertyDao.getPropertyBooleanValue(policyRoleProperty, user);
            
            if (isPolicyUsed) {
                results.add(policyRule);
            }
        }
        
        return results;
    }
    
    @Override
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user) {
        return getPasswordPolicy(user, null);
    }
    
    @Override
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user, LiteUserGroup group) {
        
        // Lets see if the passed if the new groups have a password policy.  If they do use it.
        if (group != null) {
            List<LiteYukonGroup> roleGroups = yukonGroupDao.getRoleGroupsForUserGroupId(group.getUserGroupId());
            for (LiteYukonGroup liteYukonGroup : roleGroups) {
                PasswordPolicy passwordPolicy = findPasswordPolicy(liteYukonGroup);
                if (passwordPolicy != null) {
                    return passwordPolicy;
                }
            }
        }
        
        // There is not a password policy for this user the default password policy will be returned.
        if (user == null 
                || !rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)
                || (user == null && group == null)) {
            return getDefaultPasswordPolicy();
        }
        
        return findPasswordPolicy(user);
    }
    
    @Override
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user) {
        return checkPasswordPolicy(password, user, null);
    }
    
    @Override
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user, LiteUserGroup group) {
        
        PasswordPolicy policy = getPasswordPolicy(user, group);
        if ((password.length() < policy.getMinPasswordLength()) || (password.length() > 64)) {
            return PasswordPolicyError.INVALID_PASSWORD_LENGTH;
        }
        
        // if existing user, check password age and recently used list
        if (user != null) {
            UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
            if (!policy.isPasswordAgeRequirementMet(userAuthenticationInfo)) {
                return PasswordPolicyError.MIN_PASSWORD_AGE_NOT_MET;
            }
            if (authService.isPasswordBeingReused(user, password, policy.getPasswordHistory())) {
                return PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY;
            }
        }
        
        if (authService.isPasswordBeingReused(user, password, policy.getPasswordHistory())) {
            return PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY;
        }
        
        if (!policy.isPasswordQualityCheckMet(password)) {
            return PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY;
        }
        
        return null;
    }
    
    @Override
    public Set<PasswordPolicyError> getPasswordPolicyErrors(String password, LiteYukonUser user, LiteUserGroup group) {
        
        Set<PasswordPolicyError> errors = new HashSet<PasswordPolicyError>();
        
        PasswordPolicy policy = getPasswordPolicy(user, group);
        if (password.length() < policy.getMinPasswordLength()) {
            errors.add(PasswordPolicyError.INVALID_PASSWORD_LENGTH);
        }
        
        // DB Column limit
        if (password.length() > 64) {
            errors.add(PasswordPolicyError.INVALID_PASSWORD_LENGTH);
        }
        
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        if (!policy.isPasswordAgeRequirementMet(userAuthenticationInfo)) {
            errors.add(PasswordPolicyError.MIN_PASSWORD_AGE_NOT_MET);
        }
        
        if (authService.isPasswordBeingReused(user, password, policy.getPasswordHistory())) {
            errors.add(PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY);
        }
        
        if (!policy.isPasswordQualityCheckMet(password)) {
            errors.add(PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY);
        }
        
        return errors;
    }
    
    @Override
    public Set<PolicyRule> getValidPolicyRules(String password, LiteYukonUser user, LiteUserGroup group) {
        PasswordPolicy passwordPolicy = getPasswordPolicy(user, group);
        return passwordPolicy.getValidPolicyRules(password);
    }
    
    // group may be null
    @Override
    public boolean isMinPasswordAgeMet(LiteYukonUser user, LiteUserGroup group) {
        PasswordPolicy policy = getPasswordPolicy(user, group);
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());
        return policy.isPasswordAgeRequirementMet(userAuthenticationInfo);
    }

    /**
     * Attempts to find the password policy for the user supplied.  If there is not one null will be returned. 
     */
    private PasswordPolicy findPasswordPolicy(LiteYukonUser user) {
        
        if (!rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)) {
            return null; 
        }
        
        PasswordPolicy policy = new PasswordPolicy();
        int lockoutDurationInMinutes = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.LOCKOUT_DURATION, user);
        policy.setLockoutDuration(Duration.standardMinutes(lockoutDurationInMinutes));
        policy.setLockoutThreshold(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.LOCKOUT_THRESHOLD, user));
        int maxPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MAXIMUM_PASSWORD_AGE, user);
        policy.setMaxPasswordAge(Duration.standardDays(maxPasswordAgeInDays));
        int minPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MINIMUM_PASSWORD_AGE, user);
        policy.setMinPasswordAge(Duration.standardDays(minPasswordAgeInDays));
        policy.setMinPasswordLength(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MINIMUM_PASSWORD_LENGTH, user));
        policy.setPasswordHistory(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.PASSWORD_HISTORY, user));
        policy.setPasswordQualityCheck(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.POLICY_QUALITY_CHECK, user));
        policy.setPolicyRules(getPolicyRules(user));
        
        return policy;
    }
    
    /**
     * Attempts to find the password policy for the yukon group supplied.  
     * If there is not one null will be returned. 
     */
    private PasswordPolicy findPasswordPolicy(LiteYukonGroup group) {
        
        if (!rolePropertyDao.checkRoleForRoleGroupId(YukonRole.PASSWORD_POLICY, group.getGroupID())) {
            return null; 
        }
        
        PasswordPolicy policy = new PasswordPolicy();
        int lockoutDurationInMinutes = rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.LOCKOUT_DURATION);
        policy.setLockoutDuration(Duration.standardMinutes(lockoutDurationInMinutes));
        policy.setLockoutThreshold(rolePropertyDao.getPropertyIntegerValue(group,YukonRoleProperty.LOCKOUT_THRESHOLD));
        int maxPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.MAXIMUM_PASSWORD_AGE);
        policy.setMaxPasswordAge(Duration.standardDays(maxPasswordAgeInDays));
        int minPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.MINIMUM_PASSWORD_AGE);
        policy.setMinPasswordAge(Duration.standardDays(minPasswordAgeInDays));
        policy.setMinPasswordLength(rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.MINIMUM_PASSWORD_LENGTH));
        policy.setPasswordHistory(rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.PASSWORD_HISTORY));
        policy.setPasswordQualityCheck(rolePropertyDao.getPropertyIntegerValue(group, YukonRoleProperty.POLICY_QUALITY_CHECK));
        policy.setPolicyRules(getPolicyRules(group));
        
        return policy;
    }

    /**
     * This method returns the system wide password policy.
     */
    private PasswordPolicy getDefaultPasswordPolicy() {
        
        PasswordPolicy policy = new PasswordPolicy();
        
        policy.setLockoutDuration(Duration.standardMinutes(5));
        policy.setLockoutThreshold(5);
        policy.setMaxPasswordAge(Duration.standardDays(0));
        policy.setMinPasswordAge(Duration.standardDays(0));
        policy.setMinPasswordLength(8);
        policy.setPasswordHistory(5);
        policy.setPasswordQualityCheck(3);
        policy.setPolicyRules(Lists.newArrayList(PolicyRule.values()));
        
        return policy;
    }
    
    /**
     * Gets the policy rules for a given role group, allowing us to 
     * know what rules a user must follow before they are added to the group.
     */
    private List<PolicyRule> getPolicyRules(LiteYukonGroup liteYukonGroup) {
        
        Validate.notNull(liteYukonGroup);
        
        // There is not a password policy for this user.
        if (!rolePropertyDao.checkRoleForRoleGroupId(YukonRole.PASSWORD_POLICY, liteYukonGroup.getGroupID())) {
            return getDefaultPasswordPolicy().getPolicyRules();
        }
        
        // Go through the available policies and see which ones are being use for this user. 
        List<PolicyRule> results = Lists.newArrayList();
        for (PolicyRule policyRule : PolicyRule.values()) {
            YukonRoleProperty policyRoleProperty = YukonRoleProperty.valueOf("POLICY_RULE_"+policyRule.name());
            boolean isPolicyUsed = rolePropertyDao.getPropertyBooleanValue(liteYukonGroup, policyRoleProperty);
            
            if (isPolicyUsed) {
                results.add(policyRule);
            }
        }
        
        return results;
    }
    
}