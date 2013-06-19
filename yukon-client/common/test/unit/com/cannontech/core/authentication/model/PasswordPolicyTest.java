package com.cannontech.core.authentication.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.util.UnitTestUtil;
import com.google.common.collect.Lists;

public class PasswordPolicyTest {

    private static final String PASSWORD_UPPER = "ABCDEFGH";
    private static final String PASSWORD_LOWER = "abcdefgh";
    private static final String PASSWORD_DIGITS = "12345678";
    private static final String PASSWORD_NONALPHA = "~!@#<>,.";
    private static final String PASSWORD_ONE = "ABCDabcd";
    private static final String PASSWORD_TWO = "ABCabc123";
    private static final String PASSWORD_THREE = "ABCabc123!?@";

    private static final UserAuthenticationInfo USER_JUST_CHANGED =
            new UserAuthenticationInfo(1, AuthType.HASH_SHA_V2, Instant.now().minus(Duration.standardHours(18)));
    private static final UserAuthenticationInfo USER_CHANGED_TWO_DAYS_AGO =
            new UserAuthenticationInfo(2, AuthType.HASH_SHA_V2, Instant.now().minus(Duration.standardDays(2)));
    private static final UserAuthenticationInfo USER_CHANGED_THREE_MONTHS_AGO =
            new UserAuthenticationInfo(3, AuthType.HASH_SHA_V2, Instant.now().minus(Duration.standardDays(90)));

    private static final PasswordPolicy passwordPolicyOne = new PasswordPolicy(); {
        passwordPolicyOne.setPolicyRules(Lists.newArrayList(PolicyRule.values()));
        passwordPolicyOne.setPasswordQualityCheck(3);
        passwordPolicyOne.setMaxPasswordAge(Duration.standardDays(30));
        passwordPolicyOne.setMinPasswordAge(Duration.standardDays(1));

    }
    
    @Test
    public void testUppercasePasswordPolicy() throws Exception {
        PolicyRule uppercaseCharacters = PolicyRule.UPPERCASE_CHARACTERS;
        
        Pattern uppercasePattern = uppercaseCharacters.getRegexPattern();
        Matcher uppercaseMatcher = uppercasePattern.matcher(PASSWORD_UPPER);
        
        Assert.assertTrue(uppercaseMatcher.find());
    }

    @Test
    public void testLowercasePasswordPolicy() throws Exception {
        PolicyRule lowercaseCharacters = PolicyRule.LOWERCASE_CHARACTERS;
        
        Pattern lowercasePattern = lowercaseCharacters.getRegexPattern();
        Matcher lowercaseMatcher = lowercasePattern.matcher(PASSWORD_LOWER);
        
        Assert.assertTrue(lowercaseMatcher.find());
    }

    @Test
    public void testDigitsPasswordPolicy() throws Exception {
        PolicyRule digitsCharacters = PolicyRule.BASE_10_DIGITS;
        
        Pattern digitsPattern = digitsCharacters.getRegexPattern();
        Matcher digitsMatcher = digitsPattern.matcher(PASSWORD_DIGITS);
        
        Assert.assertTrue(digitsMatcher.find());
    }

    @Test
    public void testNonalphanumericPasswordPolicy() throws Exception {
        PolicyRule nonalphanumericCharacters = PolicyRule.NONALPHANUMERIC_CHARACTERS;
        
        Pattern nonalphanumericPattern = nonalphanumericCharacters.getRegexPattern();
        Matcher nonalphanumericMatcher = nonalphanumericPattern.matcher(PASSWORD_NONALPHA);
        
        Assert.assertTrue(nonalphanumericMatcher.find());
    }

    @Test
    public void testPasswordOne() throws Exception {
        int numberOfRulesMet = passwordPolicyOne.numberOfRulesMet(PASSWORD_ONE);
        Assert.assertEquals(2, numberOfRulesMet);
        
        boolean isPasswordQualityCheckMet = passwordPolicyOne.isPasswordQualityCheckMet(PASSWORD_ONE);
        Assert.assertFalse(isPasswordQualityCheckMet);
    }
    
    @Test
    public void testPasswordTwo() throws Exception {
        int numberOfRulesMet = passwordPolicyOne.numberOfRulesMet(PASSWORD_TWO);
        Assert.assertEquals(3, numberOfRulesMet);
        
        boolean isPasswordQualityCheckMet = passwordPolicyOne.isPasswordQualityCheckMet(PASSWORD_TWO);
        Assert.assertTrue(isPasswordQualityCheckMet);
    }

    @Test
    public void testPasswordThree() throws Exception {
        int numberOfRulesMet = passwordPolicyOne.numberOfRulesMet(PASSWORD_THREE);
        Assert.assertEquals(4, numberOfRulesMet);
        
        boolean isPasswordQualityCheckMet = passwordPolicyOne.isPasswordQualityCheckMet(PASSWORD_THREE);
        Assert.assertTrue(isPasswordQualityCheckMet);
    }

    @Test
    public void testPasswordAge_JustChanged() {
        Duration passwordAge = passwordPolicyOne.getPasswordAge(USER_JUST_CHANGED);
        Assert.assertTrue(UnitTestUtil.withinOneMinute(passwordAge, Duration.standardHours(18)));

        Assert.assertFalse(passwordPolicyOne.isPasswordAgeRequirementMet(USER_JUST_CHANGED));
    }
    
    @Test
    public void testPasswordAge_TwoDaysAgo() {
        Duration passwordAge = passwordPolicyOne.getPasswordAge(USER_CHANGED_TWO_DAYS_AGO);
        Assert.assertTrue(UnitTestUtil.withinOneMinute(passwordAge, Duration.standardDays(2)));

        Assert.assertTrue(passwordPolicyOne.isPasswordAgeRequirementMet(USER_CHANGED_TWO_DAYS_AGO));
    }    

    @Test
    public void testPasswordAge_ThreeMonthsAgo() {
        Duration passwordAge = passwordPolicyOne.getPasswordAge(USER_CHANGED_THREE_MONTHS_AGO);
        Assert.assertTrue(UnitTestUtil.withinOneMinute(passwordAge, Duration.standardDays(90)));

        Assert.assertTrue(passwordPolicyOne.isPasswordAgeRequirementMet(USER_CHANGED_THREE_MONTHS_AGO));
    }
}