package com.cannontech.core.authentication.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;

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
     * This method get's the password
     */
    public Duration getPasswordAge(LiteYukonUser user) {
        Duration passwordAge = new Duration(user.getLastChangedDate(), Instant.now());
        
        return passwordAge;
    }
    
    /**
     * Checks to see if the password is old enough to be changed.
     */
    public boolean isPasswordAgeRequirementMet(LiteYukonUser user) {
        if (user == null) {
            return true;
        }
        
        Duration passwordAge = getPasswordAge(user);
        
        if (passwordAge.isLongerThan(minPasswordAge)) {
            return true;
        }
        
        return false;
    }
}