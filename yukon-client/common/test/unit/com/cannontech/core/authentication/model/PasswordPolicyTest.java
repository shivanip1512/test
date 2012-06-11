package com.cannontech.core.authentication.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PasswordPolicyTest {

    private static final String PASSWORD_UPPER = "ABCDEFGH";
    private static final String PASSWORD_LOWER = "abcdefgh";
    private static final String PASSWORD_DIGITS = "12345678";
    private static final String PASSWORD_NONALPHA = "~!@#<>,.";
//  private static final String PASSWORD_UNICODE = "ABCDabcd";
    private static final String PASSWORD_ONE = "ABCDabcd";
    private static final String PASSWORD_TWO = "ABCabc!?@";
    
    private static final PasswordPolicy passwordPolicyOne = new PasswordPolicy(); {
        passwordPolicyOne.setPolicyRules(Lists.newArrayList(PolicyRuleEnum.values()));
        passwordPolicyOne.setPasswordQualityCheck(3);

    }
    
    @Test
    public void testUppercasePasswordPolicy() throws Exception {
        PolicyRuleEnum uppercaseCharacters = PolicyRuleEnum.UPPERCASE_CHARACTERS;
        
        Pattern uppercasePattern = uppercaseCharacters.getRegexPattern();
        Matcher uppercaseMatcher = uppercasePattern.matcher(PASSWORD_UPPER);
        
        Assert.assertTrue(uppercaseMatcher.find());
    }

    @Test
    public void testLowercasePasswordPolicy() throws Exception {
        PolicyRuleEnum lowercaseCharacters = PolicyRuleEnum.LOWERCASE_CHARACTERS;
        
        Pattern lowercasePattern = lowercaseCharacters.getRegexPattern();
        Matcher lowercaseMatcher = lowercasePattern.matcher(PASSWORD_LOWER);
        
        Assert.assertTrue(lowercaseMatcher.find());
    }

    @Test
    public void testDigitsPasswordPolicy() throws Exception {
        PolicyRuleEnum digitsCharacters = PolicyRuleEnum.BASE_10_DIGITS;
        
        Pattern digitsPattern = digitsCharacters.getRegexPattern();
        Matcher digitsMatcher = digitsPattern.matcher(PASSWORD_DIGITS);
        
        Assert.assertTrue(digitsMatcher.find());
    }

    @Test
    @Ignore
    public void testNonalphanumericPasswordPolicy() throws Exception {
        PasswordPolicy passwordPolicy = new PasswordPolicy();
        passwordPolicy.setPolicyRules(Lists.newArrayList(PolicyRuleEnum.values()));
        
        PolicyRuleEnum nonalphanumericCharacters = PolicyRuleEnum.NONALPHANUMERIC_CHARACTERS;
        
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
}