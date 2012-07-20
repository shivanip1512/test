package com.cannontech.core.authentication.service;

import java.util.List;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PasswordPolicyService {
    
    /**
     * This method returns all the policy rules a user must follow when creating a password.
     */
    public List<PolicyRule> getPolicyRules(LiteYukonUser user);
    
    /**
     * This method returns the password policy for the supplied yukon user or null if the user doesn't not have a password policy.
     */
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user, LiteYukonGroup... yukonGroups);
    
    /**
     * This method will use the supplied information to generate a random password that follows the password policy for that user.
     * @throws IllegalArgumentException - This exception is thrown if a password can't be generated.
     */
    public String generatePassword(LiteYukonUser user, LiteYukonGroup... yukonGroups);

    /**
     * This method checks the supplied password against the desired password policy.  It will first try to use the
     * liteYukonGroups to figure out the password policy and then fall back onto the user's password policy.  If neither are found
     * it will use the yukon password policy.
     * 
     */
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user, LiteYukonGroup... liteYukonGroups);
}