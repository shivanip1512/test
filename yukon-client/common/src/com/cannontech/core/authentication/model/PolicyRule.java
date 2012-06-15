package com.cannontech.core.authentication.model;

import java.util.regex.Pattern;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum is used in conjunction with our role property system.  For this to work properly you will have to have a matching 
 * role property with the prefix "POLICY_RULE_".
 * 
 * Ex.  POLICY_RULE_UPPERCASE_CHARACTERS is the matching role property for UPPERCASE_CHARACTERS.
 */
public enum PolicyRule implements DisplayableEnum {
    UPPERCASE_CHARACTERS("[\\p{Lu}&&["+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]"), 
    LOWERCASE_CHARACTERS("[\\p{Ll}&&["+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]"),
    BASE_10_DIGITS("\\d"),
    NONALPHANUMERIC_CHARACTERS("\\p{Punct}"),
    UNICODE_CHARACTERS("[\\p{L}&&[^"+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]"),
    ;
    
    private static final String formatKeyPrefix = "yukon.web.modules.passwordPolicy.policyRule.";

    private Pattern regexPattern;

    PolicyRule(String regexExpressionStr) {
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