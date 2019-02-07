package com.cannontech.core.authentication.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

public class PasswordPolicy {
    
    // Lockouts/Expirations
    private Duration lockoutDuration;
    private int lockoutThreshold;
    private Duration maxPasswordAge;
    private Duration minPasswordAge;
    
    // Password Strength
    private int passwordHistory;
    private int minPasswordLength;
    private int passwordQualityCheck;
    private List<PolicyRule> policyRules;
    
    public Duration getLockoutDuration() {
        return lockoutDuration;
    }
    public void setLockoutDuration(Duration lockoutDuration) {
        this.lockoutDuration = lockoutDuration;
    }
    
    public int getLockoutThreshold() {
        return lockoutThreshold;
    }
    public void setLockoutThreshold(int lockoutThreshold) {
        this.lockoutThreshold = lockoutThreshold;
    }
    
    public Duration getMaxPasswordAge() {
        return maxPasswordAge;
    }
    public void setMaxPasswordAge(Duration maxPasswordAge) {
        this.maxPasswordAge = maxPasswordAge;
    }
    
    public Duration getMinPasswordAge() {
        return minPasswordAge;
    }
    public void setMinPasswordAge(Duration minPasswordAge) {
        this.minPasswordAge = minPasswordAge;
    }
    
    public int getPasswordHistory() {
        return passwordHistory;
    }
    public void setPasswordHistory(int passwordHistory) {
        this.passwordHistory = passwordHistory;
    }
    
    public int getMinPasswordLength() {
        return minPasswordLength;
    }
    public void setMinPasswordLength(int minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }
    
    public int getPasswordQualityCheck() {
        return passwordQualityCheck;
    }
    public void setPasswordQualityCheck(int passwordQualityCheck) {
        this.passwordQualityCheck = passwordQualityCheck;
    }
    
    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }
    public void setPolicyRules(List<PolicyRule> policyRules) {
        this.policyRules = policyRules;
    }
    
    /**
     * This method returns the number of password policy rules met by the password supplied.
     */
    public int numberOfRulesMet(String password) {
        int numberOfRulesMeet = 0;
        
        for (PolicyRule policyRule :  this.policyRules) {
            Pattern policyRolePattern = policyRule.getRegexPattern();
            Matcher matcher = policyRolePattern.matcher(password);
            
            if (matcher.find()) {
                numberOfRulesMeet++;
            }
        }
        
        return numberOfRulesMeet;
    }
    
    /**
     * This method returns whether or not the supplied password has met enough password policy rules to be accepted.
     */
    public boolean isPasswordQualityCheckMet(String password) {
        
        int numberOfRulesMet = numberOfRulesMet(password);
        if (numberOfRulesMet >= this.passwordQualityCheck) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Return a list of the PolicyRules met
     */
    public Set<PolicyRule> getValidPolicyRules(String password) {
        
        Set<PolicyRule> validRules = Sets.newHashSet();
        for (PolicyRule policyRule :  this.policyRules) {
            Pattern policyRolePattern = policyRule.getRegexPattern();
            Matcher matcher = policyRolePattern.matcher(password);
            if (matcher.find()) {
                validRules.add(policyRule);
            }
        }
        
        return validRules;
    }
    
    /**
     * Get the password age.
     */
    public Duration getPasswordAge(UserAuthenticationInfo authInfo) {
        return new Duration(authInfo.getLastChangedDate(), Instant.now());
    }
    
    /**
     * Checks to see if the password is old enough to be changed.
     */
    public boolean isPasswordAgeRequirementMet(UserAuthenticationInfo authInfo) {
        
        if (authInfo == null) {
            return true;
        }
        
        Duration passwordAge = getPasswordAge(authInfo);
        if (passwordAge.isLongerThan(minPasswordAge)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * This method will generate a password for the password policy following all the needed rules to be valid.
     * 
     * IMPORTANT NOTE:  This method will only use NONALPHANUMERIC_CHARACTERS and UNICODE_CHARACTERS characters if
     * it has to to meet the password policy requirements.  If they are not needed, the generated password will only consist
     * of alphanumeric characters.
     * If the password policy has rules defined that cannot be met (such as requiring more qualityChecks than
     * policies available),a password meeting at least the policy checks will be created.
     */
    public String generatePassword() {
        
        String genPass = PolicyRule.UPPERCASE_CHARACTERS.generateRandomCharacter();
        genPass = genPass.concat(PolicyRule.LOWERCASE_CHARACTERS.generateRandomCharacter());
        genPass = genPass.concat(PolicyRule.BASE_10_DIGITS.generateRandomCharacter());
        
        if (policyRules.contains(PolicyRule.NONALPHANUMERIC_CHARACTERS)) {
            genPass = genPass.concat(PolicyRule.NONALPHANUMERIC_CHARACTERS.generateRandomCharacter());
        }
        
        // Fill in the remaining needed characters.  If its less than 0 use 0.
        int numberOfNeededCharacters = Ints.max(passwordQualityCheck - genPass.length(), minPasswordLength - genPass.length(), 0) ;
        genPass = genPass.concat(RandomStringUtils.randomAlphanumeric(numberOfNeededCharacters));
        
        // Shuffle the characters around to make the password more random.
        List<Character> passwordCharacters = Arrays.asList(ArrayUtils.toObject(genPass.toCharArray()));
        Collections.shuffle(passwordCharacters);
        Character[] characters = passwordCharacters.toArray(new Character[0]);
        String finalGeneratedPassword = String.valueOf(ArrayUtils.toPrimitive(characters));
        
        return finalGeneratedPassword;
    }
    
}