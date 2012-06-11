package com.cannontech.core.authentication.model;

import java.util.regex.Pattern;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum is used in conjunction with our role property system.  For this to work properly you will have to have a matching 
 * role property with the prefix "POLICY_RULE_".
 * 
 * Ex.  POLICY_RULE_UPPERCASE_CHARACTERS is the matching role property for UPPERCASE_CHARACTERS.
 */
public enum PolicyRuleEnum implements DisplayableEnum {
    UPPERCASE_CHARACTERS("[A-Z]"), 
    LOWERCASE_CHARACTERS("[a-z]"),
    BASE_10_DIGITS("\\d"),
    NONALPHANUMERIC_CHARACTERS("[~!@#$%^&*_-`|(){}:;\"'<>,.?/]"), //"~!@#$%^&*_-+=`|(){}[]:;\"'<>,.?/"     Missing  -->  +=[]
    UNICODE_CHARACTERS("\\d"),
    ;
    
    private static final String formatKeyPrefix = "yukon.web.modules.policyRuleEnum..";

    private Pattern regexPattern;

    PolicyRuleEnum(String regexExpressionStr) {
        this.regexPattern = Pattern.compile(regexExpressionStr);  //, Pattern.UNICODE_CASE
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    @Override
    public String getFormatKey() {
        return formatKeyPrefix + name();
    }
}