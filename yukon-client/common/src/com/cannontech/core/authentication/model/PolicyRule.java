package com.cannontech.core.authentication.model;

import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum is used in conjunction with our role property system.  For this to work properly you will have to have a matching 
 * role property with the prefix "POLICY_RULE_".
 * 
 * Ex.  POLICY_RULE_UPPERCASE_CHARACTERS is the matching role property for UPPERCASE_CHARACTERS.
 */
public enum PolicyRule implements DisplayableEnum {
    UPPERCASE_CHARACTERS("[\\p{Lu}&&["+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]") {
        @Override
        public String generateRandomCharacter() {
            return RandomStringUtils.random(1, 65, 90, true, false);
        }
    }, 
    LOWERCASE_CHARACTERS("[\\p{Ll}&&["+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]") {
        @Override
        public String generateRandomCharacter() {
            return RandomStringUtils.random(1, 97, 122, true, false);
        }
    },
    BASE_10_DIGITS("\\d") {
        @Override
        public String generateRandomCharacter() {
            return RandomStringUtils.random(1, 48, 57, false, true);
        }
    },
    NONALPHANUMERIC_CHARACTERS("\\p{Punct}") {
        @Override
        public String generateRandomCharacter() {
            return RandomStringUtils.random(1, 32, 47, false, false);
        }
    },
    UNICODE_CHARACTERS("[\\p{L}&&[^"+PolicyRuleRegexSupport.EUROPEAN_LANG_CHAR_REGEX+"]]") {
        @Override
        public String generateRandomCharacter() {
            return RandomStringUtils.random(1, 250, 5000, false, false);
        }
    },
    ;
    
    private static final String formatKeyPrefix = "yukon.web.modules.passwordPolicy.policyRule.";

    private Pattern regexPattern;

    PolicyRule(String regexExpressionStr) {
        this.regexPattern = Pattern.compile(regexExpressionStr);  //, Pattern.UNICODE_CASE
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public abstract String generateRandomCharacter();
    
    @Override
    public String getFormatKey() {
        return formatKeyPrefix + name();
    }
}