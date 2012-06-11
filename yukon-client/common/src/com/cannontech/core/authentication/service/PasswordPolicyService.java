package com.cannontech.core.authentication.service;

import java.util.List;

import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PolicyRuleEnum;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PasswordPolicyService {
    
    /**
     * This method returns all the policy rules a user must follow when creating a password.
     */
    public List<PolicyRuleEnum> getPolicyRules(LiteYukonUser user);
    
    /**
     * This method returns the password policy for the supplied yukon user or null if the user doesn't not have a password policy.
     */
    public PasswordPolicy findPasswordPolicy(LiteYukonUser user);
}