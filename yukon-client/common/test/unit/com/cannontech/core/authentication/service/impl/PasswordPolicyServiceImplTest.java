package com.cannontech.core.authentication.service.impl;

import static com.cannontech.core.roleproperties.YukonRoleProperty.*;
import static com.cannontech.core.roleproperties.YukonRole.*;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.UnitTestUtil;
import com.google.common.collect.Lists;

public class PasswordPolicyServiceImplTest {
    
    private static final Instant EIGHTEEN_HOURS_AGO = Instant.now().minus(Duration.standardHours(18));
    private static final Instant TWO_DAYS_AGO = Instant.now().minus(Duration.standardDays(2));
    private static final Instant FIFTY_DAYS_AGO = Instant.now().minus(Duration.standardDays(50));
    private static final Instant ONE_HUNDRED_DAYS_AGO = Instant.now().minus(Duration.standardDays(80));
    
    private static final LiteYukonUser USER_ONE = new LiteYukonUser(1, "userOne");
    private static final UserAuthenticationInfo USER_ONE_AUTH_INFO =
            new UserAuthenticationInfo(1, AuthType.HASH_SHA_V2, EIGHTEEN_HOURS_AGO);

    private static final LiteYukonUser USER_TWO = new LiteYukonUser(2, "userTwo");
    private static final UserAuthenticationInfo USER_TWO_AUTH_INFO =
            new UserAuthenticationInfo(2, AuthType.HASH_SHA_V2, TWO_DAYS_AGO);

    private static final LiteYukonUser USER_NO_POLICY = new LiteYukonUser(3, "userNoPolicy");
    private static final UserAuthenticationInfo USER_NO_POLICY_AUTH_INFO =
            new UserAuthenticationInfo(3, AuthType.HASH_SHA_V2, ONE_HUNDRED_DAYS_AGO);

    private static final LiteYukonUser USER_SYSTEM_POLICY = new LiteYukonUser(4, "userSystemPolicy");
    private static final UserAuthenticationInfo USER_SYSTEM_POLICY_AUTH_INFO =
            new UserAuthenticationInfo(4, AuthType.HASH_SHA_V2, FIFTY_DAYS_AGO);

    private static final MockRolePropertyDaoImpl rolePropertyDaoMock = new MockRolePropertyDaoImpl();
    { 
        rolePropertyDaoMock.setupRolesFor(USER_ONE)
            .withRoleProperty(LOCKOUT_DURATION, 6)
            .withRoleProperty(LOCKOUT_THRESHOLD, 3)
            .withRoleProperty(MAXIMUM_PASSWORD_AGE, 20)
            .withRoleProperty(MINIMUM_PASSWORD_AGE, 1)
            .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 6)
            .withRoleProperty(PASSWORD_HISTORY, 3)
            .withRoleProperty(POLICY_QUALITY_CHECK, 2)
    
            .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, true)
            .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, false)
            .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, true)
            .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, true);
    
        rolePropertyDaoMock.setupRolesFor(USER_TWO)
            .withRoleProperty(LOCKOUT_DURATION, 120)
            .withRoleProperty(LOCKOUT_THRESHOLD, 10)
            .withRoleProperty(MAXIMUM_PASSWORD_AGE, 15)
            .withRoleProperty(MINIMUM_PASSWORD_AGE, 0)
            .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 9)
            .withRoleProperty(PASSWORD_HISTORY, 10)
            .withRoleProperty(POLICY_QUALITY_CHECK, 1)
    
            .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, false)
            .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, true)
            .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, false)
            .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, false);
        
        rolePropertyDaoMock.setupRolesFor(USER_NO_POLICY)
            .withRoleProperty(LOCKOUT_DURATION, 0)
            .withRoleProperty(LOCKOUT_THRESHOLD, 0)
            .withRoleProperty(MAXIMUM_PASSWORD_AGE, 0)
            .withRoleProperty(MINIMUM_PASSWORD_AGE, 0)
            .withRoleProperty(MINIMUM_PASSWORD_LENGTH, 0)
            .withRoleProperty(PASSWORD_HISTORY, 0)
            .withRoleProperty(POLICY_QUALITY_CHECK, 0)
    
            .withRoleProperty(POLICY_RULE_UPPERCASE_CHARACTERS, true)
            .withRoleProperty(POLICY_RULE_LOWERCASE_CHARACTERS, true)
            .withRoleProperty(POLICY_RULE_BASE_10_DIGITS, true)
            .withRoleProperty(POLICY_RULE_NONALPHANUMERIC_CHARACTERS, true);
    
        rolePropertyDaoMock.setupRolesFor(USER_SYSTEM_POLICY)
            .withRole(PASSWORD_POLICY, false);
    }

    public static final PasswordPolicyService passwordPolicyService = new PasswordPolicyServiceImpl(); 
    {
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

        Assert.assertTrue(UnitTestUtil.withinOneSecond(new Duration(EIGHTEEN_HOURS_AGO, Instant.now()),
                                                       passwordPolicy.getPasswordAge(USER_ONE_AUTH_INFO)));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());

    }

    @Test
    public void testPasswordPolicy_UserTwo() {
        List<PolicyRule> expectedPolicyRules = 
                Lists.newArrayList(PolicyRule.LOWERCASE_CHARACTERS);

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
        Assert.assertTrue(UnitTestUtil.withinOneSecond(new Duration(TWO_DAYS_AGO, Instant.now()),
                                                       passwordPolicy.getPasswordAge(USER_TWO_AUTH_INFO)));
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
        Assert.assertTrue(UnitTestUtil.withinOneSecond(new Duration(ONE_HUNDRED_DAYS_AGO, Instant.now()),
                                                       passwordPolicy.getPasswordAge(USER_NO_POLICY_AUTH_INFO)));
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
        Assert.assertEquals(Duration.standardDays(0), passwordPolicy.getMaxPasswordAge());
        Assert.assertEquals(Duration.ZERO, passwordPolicy.getMinPasswordAge());
        Assert.assertEquals(8, passwordPolicy.getMinPasswordLength());
        Assert.assertEquals(5, passwordPolicy.getPasswordHistory());
        Assert.assertEquals(3, passwordPolicy.getPasswordQualityCheck());
        Assert.assertTrue(UnitTestUtil.withinOneSecond(new Duration(FIFTY_DAYS_AGO, Instant.now()),
                                                       passwordPolicy.getPasswordAge(USER_SYSTEM_POLICY_AUTH_INFO)));
        Assert.assertEquals(expectedPolicyRules, passwordPolicy.getPolicyRules());
        
    }
}