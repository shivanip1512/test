package com.cannontech.core.authentication.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PasswordPolicyTest {

    private static final String PASSWORD_UPPER = "ABCDEFGH";
    private static final String PASSWORD_LOWER = "abcdefgh";
    private static final String PASSWORD_DIGITS = "12345678";
    private static final String PASSWORD_NONALPHA = "~!@#<>,.";
    private static final String PASSWORD_UNICODE = "\u0E01\u0E02\u0E03\u0E04\u0E05\u0E06\u0E07\u0E08";
    private static final String PASSWORD_ONE = "ABCDabcd";
    private static final String PASSWORD_TWO = "ABCabc123";
    private static final String PASSWORD_THREE = "ABCabc123!?@";
    private static final String PASSWORD_FOUR = "ABCabc123!?@\u0E01\u0E02\u0E03";
    
    private static final PasswordPolicy passwordPolicyOne = new PasswordPolicy(); {
        passwordPolicyOne.setPolicyRules(Lists.newArrayList(PolicyRule.values()));
        passwordPolicyOne.setPasswordQualityCheck(3);

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
    public void testUnicodePasswordPolicy() throws Exception {
        PolicyRule unicodeCharacters = PolicyRule.UNICODE_CHARACTERS;
        
        Pattern unicodePattern = unicodeCharacters.getRegexPattern();
        Matcher unicodeMatcher = unicodePattern.matcher(PASSWORD_UNICODE);
        
        Assert.assertTrue(unicodeMatcher.find());
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
    public void testPasswordFour() throws Exception {
        int numberOfRulesMet = passwordPolicyOne.numberOfRulesMet(PASSWORD_FOUR);
        Assert.assertEquals(5, numberOfRulesMet);
        
        boolean isPasswordQualityCheckMet = passwordPolicyOne.isPasswordQualityCheckMet(PASSWORD_FOUR);
        Assert.assertTrue(isPasswordQualityCheckMet);
    }

}