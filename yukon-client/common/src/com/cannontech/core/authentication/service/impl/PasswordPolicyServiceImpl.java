package com.cannontech.core.authentication.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private RolePropertyDao rolePropertyDao;

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
    
    /**
     * This method gets the policy rules for a given role group, allowing us to know what rules a user must follow before
     * they are added to the group.
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
    
    @Override
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user, LiteYukonGroup... yukonGroups) {
        
    	// Lets see if the passed if the new groups have a password policy.  If they do use it.
        if (yukonGroups != null) {
            for (LiteYukonGroup liteYukonGroup : yukonGroups) {
                PasswordPolicy passwordPolicy = findPasswordPolicy(liteYukonGroup);
                if (passwordPolicy != null) {
                    return passwordPolicy;
                }
            }
        }
        
        // There is not a password policy for this user the default password policy will be returned.
        if (user == null || !rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)) {
            return getDefaultPasswordPolicy();
        }
        
        return findPasswordPolicy(user);
    }

    @Override
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user, LiteYukonGroup... liteYukonGroups) {
        
        PasswordPolicy passwordPolicy = getPasswordPolicy(user, liteYukonGroups);
        if (password.length() < passwordPolicy.getMinPasswordLength()) {
            return PasswordPolicyError.MIN_PASSWORD_LENGTH_NOT_MET;
        }
        
        if (!passwordPolicy.isPasswordAgeRequirementMet(user)) {
            return PasswordPolicyError.MIN_PASSWORD_AGE_NOT_MET;
        }
        
        if (authenticationService.isPasswordBeingReused(user, password, passwordPolicy.getPasswordHistory())) {
            return PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY;
        }
        
        if (!passwordPolicy.isPasswordQualityCheckMet(password)) {
            return PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY;
        }
        
        return null;
    }
    
    /**
     * Attempts to find the password policy for the user supplied.  If there is not one null will be returned. 
     */
    private PasswordPolicy findPasswordPolicy(LiteYukonUser user) {
        if (!rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)) {
            return null; 
        }
        
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        int lockoutDurationInMinutes = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.LOCKOUT_DURATION, user);
        passwordPolicy.setLockoutDuration(Duration.standardMinutes(lockoutDurationInMinutes));
        passwordPolicy.setLockoutThreshold(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.LOCKOUT_THRESHOLD, user));
        int maxPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MAXIMUM_PASSWORD_AGE, user);
        passwordPolicy.setMaxPasswordAge(Duration.standardDays(maxPasswordAgeInDays));
        int minPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MINIMUM_PASSWORD_AGE, user);
        passwordPolicy.setMinPasswordAge(Duration.standardDays(minPasswordAgeInDays));
        passwordPolicy.setMinPasswordLength(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MINIMUM_PASSWORD_LENGTH, user));
        passwordPolicy.setPasswordHistory(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.PASSWORD_HISTORY, user));
        passwordPolicy.setPasswordQualityCheck(rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.POLICY_QUALITY_CHECK, user));
        passwordPolicy.setPolicyRules(getPolicyRules(user));
        
        return passwordPolicy;
    }
    
    /**
     * Attempts to find the password policy for the yukon group supplied.  If there is not one null will be returned. 
     */
    private PasswordPolicy findPasswordPolicy(LiteYukonGroup liteYukonGroup) {
        if (!rolePropertyDao.checkRoleForRoleGroupId(YukonRole.PASSWORD_POLICY, liteYukonGroup.getGroupID())) {
            return null; 
        }
        
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        int lockoutDurationInMinutes = rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.LOCKOUT_DURATION);
        passwordPolicy.setLockoutDuration(Duration.standardMinutes(lockoutDurationInMinutes));
        passwordPolicy.setLockoutThreshold(rolePropertyDao.getPropertyIntegerValue(liteYukonGroup,YukonRoleProperty.LOCKOUT_THRESHOLD));
        int maxPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.MAXIMUM_PASSWORD_AGE);
        passwordPolicy.setMaxPasswordAge(Duration.standardDays(maxPasswordAgeInDays));
        int minPasswordAgeInDays = rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.MINIMUM_PASSWORD_AGE);
        passwordPolicy.setMinPasswordAge(Duration.standardDays(minPasswordAgeInDays));
        passwordPolicy.setMinPasswordLength(rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.MINIMUM_PASSWORD_LENGTH));
        passwordPolicy.setPasswordHistory(rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.PASSWORD_HISTORY));
        passwordPolicy.setPasswordQualityCheck(rolePropertyDao.getPropertyIntegerValue(liteYukonGroup, YukonRoleProperty.POLICY_QUALITY_CHECK));
        passwordPolicy.setPolicyRules(getPolicyRules(liteYukonGroup));

        return passwordPolicy;
    }

    /**
     * This method returns the system wide password policy.
     */
    private PasswordPolicy getDefaultPasswordPolicy() {
        PasswordPolicy passwordPolicy = new PasswordPolicy();

        passwordPolicy.setLockoutDuration(Duration.standardMinutes(5));
        passwordPolicy.setLockoutThreshold(5);
        passwordPolicy.setMaxPasswordAge(Duration.standardDays(0));
        passwordPolicy.setMinPasswordAge(Duration.standardDays(0));
        passwordPolicy.setMinPasswordLength(8);
        passwordPolicy.setPasswordHistory(5);
        passwordPolicy.setPasswordQualityCheck(3);
        passwordPolicy.setPolicyRules(Lists.newArrayList(PolicyRule.values()));
        
        return passwordPolicy;
    }
}