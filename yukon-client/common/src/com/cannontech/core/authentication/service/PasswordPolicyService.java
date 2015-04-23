package com.cannontech.core.authentication.service;

import java.util.List;
import java.util.Set;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.model.PolicyRule;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PasswordPolicyService {
    
    /**
     * This method returns all the policy rules a user must follow when creating a password.
     */
    public List<PolicyRule> getPolicyRules(LiteYukonUser user);
    
    /**
     * This method returns the password policy for the supplied yukon user or null if the user doesn't 
     * not have a password policy.
     */
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user);

    /**
     * This method returns the password policy for the supplied yukon user 
     * or the default policy if one cannot be resolved.
     */
    public PasswordPolicy getPasswordPolicy(LiteYukonUser user, LiteUserGroup group);

    /**
     * This method checks the supplied password against the desired password policy.  It will first try to use the
     * user's password policy and then fall back to the yukon password policy.
     */
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user);
    
    /**
     * This method checks the supplied password against the desired password policy.  It will first try to use the
     * liteUserGroup to figure out the password policy and then fall back onto the user's password policy.  
     * If neither are found it will use the yukon password policy.
     */
    public PasswordPolicyError checkPasswordPolicy(String password, LiteYukonUser user, LiteUserGroup group);

    /**
     * This method checks the supplied password against the desired password policy.  It will first try to use the
     * liteUserGroup to figure out the password policy and then fall back onto the user's password policy.  
     * If neither are found it will use the yukon password policy.
     */
    public Set<PasswordPolicyError> getPasswordPolicyErrors(String password, LiteYukonUser user, LiteUserGroup group);

    /**
     * Get the valid (passed) policy rules for a given user, group and password. It will first try to use the
     * liteUserGroup to figure out the password policy and then fall back onto the user's password policy.  
     * If neither are found it will use the yukon password policy.
     */
    public Set<PolicyRule> getValidPolicyRules(String password, LiteYukonUser user, LiteUserGroup group);
}