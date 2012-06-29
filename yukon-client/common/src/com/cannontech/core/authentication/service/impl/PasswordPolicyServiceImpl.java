package com.cannontech.core.authentication.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class PasswordPolicyServiceImpl implements PasswordPolicyService {

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
    
    @Override
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user) {
        // There is not a password policy for this user the default password policy will be returned.
        if (user == null || !rolePropertyDao.checkRole(YukonRole.PASSWORD_POLICY, user)) {
            return getDefaultPasswordPolicy();
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
     * This method returns the system wide password policy.
     */
    private PasswordPolicy getDefaultPasswordPolicy() {
        PasswordPolicy passwordPolicy = new PasswordPolicy();

        passwordPolicy.setLockoutDuration(Duration.standardMinutes(5));
        passwordPolicy.setLockoutThreshold(5);
        passwordPolicy.setMaxPasswordAge(Duration.standardDays(180));
        passwordPolicy.setMinPasswordAge(Duration.standardDays(0));
        passwordPolicy.setMinPasswordLength(8);
        passwordPolicy.setPasswordHistory(5);
        passwordPolicy.setPasswordQualityCheck(3);
        passwordPolicy.setPolicyRules(Lists.newArrayList(PolicyRule.values()));
        
        return passwordPolicy;
    }
}