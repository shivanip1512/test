package com.cannontech.core.authentication.service.impl;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.dao.impl.RolePropertyDaoImpl;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class PasswordPolicyServiceImplTest {
    
    private static final Instant EIGHTEN_HOURS_AGO = Instant.now().minus(Duration.standardHours(18));
    private static final Instant TWO_DAYS_AGO = Instant.now().minus(Duration.standardDays(2));
    private static final Instant FIFTY_DAYS_AGO = Instant.now().minus(Duration.standardDays(50));
    private static final Instant ONE_HUNDRED_DAYS_AGO = Instant.now().minus(Duration.standardDays(80));
    
    LiteYukonUser USER_ONE = new LiteYukonUser();{
        USER_ONE.setUserID(1);
        USER_ONE.setLastChangedDate(EIGHTEN_HOURS_AGO);        
    }

    LiteYukonUser USER_TWO = new LiteYukonUser();{
        USER_TWO.setUserID(2);
        USER_TWO.setLastChangedDate(TWO_DAYS_AGO);        
    }

    LiteYukonUser USER_NO_POLICY = new LiteYukonUser();{
        USER_NO_POLICY.setUserID(3);
        USER_NO_POLICY.setLastChangedDate(ONE_HUNDRED_DAYS_AGO);        
    }

    LiteYukonUser USER_SYSTEM_POLICY = new LiteYukonUser();{
        USER_SYSTEM_POLICY.setUserID(4);
        USER_SYSTEM_POLICY.setLastChangedDate(FIFTY_DAYS_AGO);        
    }
    
    PasswordPolicyService passwordPolicyService = new PasswordPolicyServiceImpl(); {
        RolePropertyDao rolePropertyDaoMock = new RolePropertyDaoImpl() {
            @Override
            public boolean checkRole(YukonRole role, LiteYukonUser user) {
                if (user.equals(USER_ONE) || user.equals(USER_TWO) || user.equals(USER_NO_POLICY)) {
                    return true;
                }
                return false;
            }
            
            @Override
            public int getPropertyIntegerValue(YukonRoleProperty rolePropertyValue, LiteYukonUser user)
            throws UserNotInRoleException {
                if (user.equals(USER_ONE)) {
                    switch (rolePropertyValue) {
                        case LOCKOUT_DURATION:
                            return 6;
                        case LOCKOUT_THRESHOLD:
                            return 3;
                        case MAXIMUM_PASSWORD_AGE:
                            return 20;
                        case MINIMUM_PASSWORD_AGE:
                            return 1;
                        case MINIMUM_PASSWORD_LENGTH:
                            return 6;
                        case PASSWORD_HISTORY:
                            return 3;
                        case POLICY_QUALITY_CHECK:
                            return 2;
                    }
                }
                
                if (user.equals(USER_TWO)) {
                    switch (rolePropertyValue) {
                        case LOCKOUT_DURATION:
                            return 120;
                        case LOCKOUT_THRESHOLD:
                            return 10;
                        case MAXIMUM_PASSWORD_AGE:
                            return 15;
                        case MINIMUM_PASSWORD_AGE:
                            return 0;
                        case MINIMUM_PASSWORD_LENGTH:
                            return 9;
                        case PASSWORD_HISTORY:
                            return 10;
                        case POLICY_QUALITY_CHECK:
                            return 1;
                    }
                }

                if (user.equals(USER_NO_POLICY)) {
                    switch (rolePropertyValue) {
                        case LOCKOUT_DURATION:
                            return 0;
                        case LOCKOUT_THRESHOLD:
                            return 0;
                        case MAXIMUM_PASSWORD_AGE:
                            return 0;
                        case MINIMUM_PASSWORD_AGE:
                            return 0;
                        case MINIMUM_PASSWORD_LENGTH:
                            return 0;
                        case PASSWORD_HISTORY:
                            return 0;
                        case POLICY_QUALITY_CHECK:
                            return 0;
                    }
                }

                // Role Property Does Not Yet Exist
                throw new RuntimeErrorException(null, "The role property does not exist in the mock role property dao.");
            }

            @Override
            public boolean getPropertyBooleanValue(YukonRoleProperty rolePropertyValue, LiteYukonUser user)
            throws UserNotInRoleException {
                if (user.equals(USER_ONE)) {
                    switch (rolePropertyValue) {
                        case POLICY_RULE_UPPERCASE_CHARACTERS:
                            return true;
                        case POLICY_RULE_LOWERCASE_CHARACTERS:
                            return false;
                        case POLICY_RULE_BASE_10_DIGITS:
                            return true;
                        case POLICY_RULE_NONALPHANUMERIC_CHARACTERS:
                            return true;
                        case POLICY_RULE_UNICODE_CHARACTERS:
                            return false;
                    }
                }
 
                if (user.equals(USER_TWO)) {
                    switch (rolePropertyValue) {
                        case POLICY_RULE_UPPERCASE_CHARACTERS:
                            return false;
                        case POLICY_RULE_LOWERCASE_CHARACTERS:
                            return true;
                        case POLICY_RULE_BASE_10_DIGITS:
                            return false;
                        case POLICY_RULE_NONALPHANUMERIC_CHARACTERS:
                            return false;
                        case POLICY_RULE_UNICODE_CHARACTERS:
                            return true;
                    }
                }

                if (user.equals(USER_NO_POLICY)) {
                    switch (rolePropertyValue) {
                        case POLICY_RULE_UPPERCASE_CHARACTERS:
                            return true;
                        case POLICY_RULE_LOWERCASE_CHARACTERS:
                            return true;
                        case POLICY_RULE_BASE_10_DIGITS:
                            return true;
                        case POLICY_RULE_NONALPHANUMERIC_CHARACTERS:
                            return true;
                        case POLICY_RULE_UNICODE_CHARACTERS:
                            return true;
                    }
                }

                // Role Property Does Not Yet Exist
                throw new RuntimeErrorException(null, "The role property does not exist in the mock role property dao.");
            }
        };
        
        ReflectionTestUtils.setField(passwordPolicyService, "rolePropertyDao", rolePropertyDaoMock);
    }
    
    @Test
    public void testPasswordPolicy_UserOne() {
        List<PolicyRule> expectedPolicyRules = 
                Lists.newArrayList(PolicyRule.UPPERCASE_CHARACTERS, PolicyRule.BASE_10_DIGITS, PolicyRule.NONALPHANUMERIC_CHARACTERS);

        // Checking policy rules
        List<PolicyRule> policyRules = passwordPolicyService.getPolicyRules(USER_ONE);
        Assert.assertEquals(expectedPolicyRules, policyRules);

        // Checking password policy
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(USER_ONE);
        Assert.assertEquals(Duration.standardMinutes(6), passwordPolicy.getLockoutDuration());
        Assert.assertEquals(3, passwordPolicy.getLockoutThreshold());
        Assert.assertEquals(Duration.standardDays(20), passwordPolicy.getMaxPasswordAge());
        Assert.assertEquals(Duration.standardDays(1), passwordPolicy.getMinPasswordAge());
        Assert.assertEquals(6, passwordPolicy.getMinPasswordLength());
        Assert.assertEquals(3, passwordPolicy.getPasswordHistory());
        Assert.assertEquals(2, passwordPolicy.getPasswordQualityCheck());
        Assert.assertEquals(new Duration(EIGHTEN_HOURS_AGO, Instant.now()), passwordPolicy.getPasswordAge(USER_ONE));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());

    }

    @Test
    public void testPasswordPolicy_UserTwo() {
        List<PolicyRule> expectedPolicyRules = 
                Lists.newArrayList(PolicyRule.LOWERCASE_CHARACTERS, PolicyRule.UNICODE_CHARACTERS);

        // Checking policy rules
        List<PolicyRule> policyRules = passwordPolicyService.getPolicyRules(USER_TWO);
        Assert.assertEquals(expectedPolicyRules, policyRules);

        // Checking password policy
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(USER_TWO);
        Assert.assertEquals(Duration.standardMinutes(120), passwordPolicy.getLockoutDuration());
        Assert.assertEquals(10, passwordPolicy.getLockoutThreshold());
        Assert.assertEquals(Duration.standardDays(15), passwordPolicy.getMaxPasswordAge());
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getMinPasswordAge());
        Assert.assertEquals(9, passwordPolicy.getMinPasswordLength());
        Assert.assertEquals(10, passwordPolicy.getPasswordHistory());
        Assert.assertEquals(1, passwordPolicy.getPasswordQualityCheck());
        Assert.assertEquals(new Duration(TWO_DAYS_AGO, Instant.now()), passwordPolicy.getPasswordAge(USER_TWO));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());

    }

    @Test
    public void testPasswordPolicy_NoPolicy() {
        List<PolicyRule> expectedPolicyRules = Lists.newArrayList(PolicyRule.values());

        // Checking policy rules
        List<PolicyRule> policyRules = passwordPolicyService.getPolicyRules(USER_NO_POLICY);
        Assert.assertEquals(expectedPolicyRules, policyRules);

        // Checking password policy
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(USER_NO_POLICY);
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getLockoutDuration());
        Assert.assertEquals(0, passwordPolicy.getLockoutThreshold());
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getMaxPasswordAge());
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getMinPasswordAge());
        Assert.assertEquals(0, passwordPolicy.getMinPasswordLength());
        Assert.assertEquals(0, passwordPolicy.getPasswordHistory());
        Assert.assertEquals(0, passwordPolicy.getPasswordQualityCheck());
        Assert.assertEquals(new Duration(ONE_HUNDRED_DAYS_AGO, Instant.now()), passwordPolicy.getPasswordAge(USER_NO_POLICY));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());
    }
    
    @Test
    public void testPasswordPolicy_System() {
        List<PolicyRule> expectedPolicyRules = Lists.newArrayList(PolicyRule.values());

        // Checking policy rules
        List<PolicyRule> policyRules = passwordPolicyService.getPolicyRules(USER_SYSTEM_POLICY);
        Assert.assertEquals(expectedPolicyRules, policyRules);

        // Checking password policy
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(USER_SYSTEM_POLICY);
        Assert.assertEquals(Duration.standardMinutes(5), passwordPolicy.getLockoutDuration());
        Assert.assertEquals(5, passwordPolicy.getLockoutThreshold());
        Assert.assertEquals(Duration.standardDays(180), passwordPolicy.getMaxPasswordAge());
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getMinPasswordAge());
        Assert.assertEquals(8, passwordPolicy.getMinPasswordLength());
        Assert.assertEquals(5, passwordPolicy.getPasswordHistory());
        Assert.assertEquals(3, passwordPolicy.getPasswordQualityCheck());
        Assert.assertEquals(new Duration(FIFTY_DAYS_AGO, Instant.now()), passwordPolicy.getPasswordAge(USER_SYSTEM_POLICY));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());
        
    }
}