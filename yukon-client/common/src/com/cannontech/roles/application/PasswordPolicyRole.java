package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

public interface PasswordPolicyRole {
    public static final int ROLEID = ApplicationRoleDefs.PASSWORD_POLICY_ROLEID;        
    
    public static final int PASSWORD_HISTORY = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE -1;
    public static final int MINIMUM_PASSWORD_LENGTH = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 2;
    public static final int MINIMUM_PASSWORD_AGE = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 3;
    public static final int MAXIMUM_PASSWORD_AGE = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 4;
    public static final int LOCKOUT_THRESHOLD = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 5;
    public static final int LOCKOUT_DURATION = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 6;
    
    public static final int POLICY_QUALITY_CHECK = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 50;
    public static final int POLICY_RULE_UPPERCASE_CHARACTERS = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 51;
    public static final int POLICY_RULE_LOWERCASE_CHARACTERS = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 52;
    public static final int POLICY_RULE_BASE_10_DIGITS = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 53;
    public static final int POLICY_RULE_NONALPHANUMERIC_CHARACTERS = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 54;
    public static final int POLICY_RULE_UNICODE_CHARACTERS = ApplicationRoleDefs.PASSWORD_POLICY_PROPERTYID_BASE - 55;

}