package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.util.Range;

public class YukonValidationUtilsCommon extends ValidationUtils {
   
    public static final String BASIC_BLACKLISTED_CHAR_LIST = "[\\\\!#$%&'*();+=<>?{}\"|,/]";
    public final static String ILLEGAL_NAME_CHARS = "[\\,|/\"\\\\]";
    public final static String ILLEGAL_XML_CHARS = "[\"'<>&]";

    public static  boolean checkExceedsMaxLength(String fieldValue, int max) {
        return (fieldValue != null && fieldValue.length() > max) ? true : false;
    }

    /**
     * Return true if the provided String contains any characters from blacklisted characters( \\, !, #, $, %, &, ', *, (, ), ;,
     * +, =, <, >, ?, {, }, \, ", |, / and , ).
     */
    public static boolean checkBlacklistedCharacter(String fieldValue) {
        if (fieldValue != null) {
            Matcher hasBlacklistedChar = Pattern.compile(BASIC_BLACKLISTED_CHAR_LIST).matcher(fieldValue);
            return (hasBlacklistedChar.find()) ? true : false;
        }
        return false;
    }
    
    /**
     * Return true if the provided String contains only characters from whitelisted characters(charcters must be A-Z, a-z, 0-9, ., $ and _ ).
     */
    public static boolean checkWhitelistedCharacter(String fieldValue) {
        if (fieldValue != null) {
            String whitelist = "^[a-zA-Z0-9_$.]+$";
            Matcher isWhitelistedChars = Pattern.compile(whitelist).matcher(fieldValue);
            return isWhitelistedChars.matches() ? true : false;
        }
        return false;
    }

    /**
     * Return true if the provided String contains any characters from illegal characters( \, |, /, ", \\ and , ).
     */
    public static boolean checkIllegalCharacter(String fieldValue) {
        if (fieldValue != null) {
            Matcher hasBlacklistedChar = Pattern.compile(ILLEGAL_NAME_CHARS).matcher(fieldValue);
            return (hasBlacklistedChar.find()) ? true : false;
        }
        return false;
    }
    
    public static boolean checkHexOnlyCharacter(String fieldValue) {
        if (fieldValue != null) {
            String whitelist = "^[a-fA-F0-9]+$";
            Matcher isWhitelistedChars = Pattern.compile(whitelist).matcher(fieldValue);
            return !isWhitelistedChars.matches();
        }
        return false;
    }

    /**
     * Return true if the provided String contains any characters from illegal XML characters ( " ' < > and & )
     */
    public static boolean checkIllegalXmlCharacter(String fieldValue) {
        if (fieldValue != null) {
            Matcher hasBlacklistedChar = Pattern.compile(ILLEGAL_XML_CHARS).matcher(fieldValue);
            return hasBlacklistedChar.find();
        }
        return false;
    }

    /**
     * @deprecated - Use {LINK #checkIsBlank(String fieldValue, boolean fieldAllowsNull)}
     */
    // @Deprecated(since="7.5", forRemoval=true)
    @Deprecated
    public static boolean checkIsBlank(String fieldValue, boolean fieldAllowsNull) {
        // Skips error message when the field allows null and the field value is null,
        // otherwise validates using isBlank.
        return (!(fieldAllowsNull && fieldValue == null) && StringUtils.isBlank(fieldValue)) ? true : false;
    }
    
    public static boolean checkIsPositiveShort(Short fieldValue) {
        return (fieldValue == null || fieldValue < 0) ? true : false;
    }

    public static boolean checkIsPositiveInt(Integer fieldValue) {
        return (fieldValue == null || fieldValue < 0) ? true : false;
    }

    public static boolean checkIsPositiveDouble(Double fieldValue) {
        return (checkIsValidDouble(fieldValue) && fieldValue < 0) ? true : false;
    }

    public static boolean checkIsValidDouble(Double fieldValue) {
        return (fieldValue == null || Double.isNaN(fieldValue) || Double.isInfinite(fieldValue)) ? false : true;
    }

    public static boolean checkIsNumberPositiveDouble(Number fieldValue) {
           return (!checkIsValidDouble(fieldValue.doubleValue()) || fieldValue.doubleValue() < 0) ? false : true;
    }
    
    public static boolean checkIsNumberPositiveInt(Number fieldValue) {
        return (fieldValue.intValue() < 0) ? false : true;
    }

    public static <T extends Comparable<T>> boolean checkRange(T fieldValue, T min, T max) {
        return (fieldValue.compareTo(min) < 0 || fieldValue.compareTo(max) > 0) ? true : false;
    }

    /**
     * Check to ensure that the given value is between the given range, expects a fully inclusive Range.
     */
    public static <T extends Comparable<T>> boolean checkRange(T fieldValue, Range<T> range) {
        return (fieldValue == null || (fieldValue != null && !range.intersects(fieldValue))) ? true : false;
    }

    public static boolean ipHostNameValidator(Errors errors, String field, String fieldValue) {
        Pattern ipHostNameMatcher = Pattern.compile(
                "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");
        if (!errors.hasFieldErrors(field)) {
            if (!ipHostNameMatcher.matcher(fieldValue).matches()) {
               return true;
            }
        }
        return false;
    }

    /* Validate string for exact length. */
    public static boolean checkExactLength(String fieldValue, int stringLength) {
        return (fieldValue != null && fieldValue.length() != stringLength) ? false : true;
    }

    /* Validate a required list is empty */
    public static boolean checkIfListRequired(List<?> fieldValue) {
        return (fieldValue == null || fieldValue.isEmpty()) ? true : false;
    }

    /* Validate field is required */
    public static boolean checkIfFieldRequired(Object fieldValue) {
        return (fieldValue == null || StringUtils.isBlank(fieldValue.toString())) ? true : false;
    }

    /**
     * Check if fieldValue <= targetValue
     * 
     * @param fieldValue      - value of field
     * @param targetValue     - value you are testing if fieldValue is greater then
     */
    public static boolean checkIsFieldValueGreaterThenTargetValueInt(Integer fieldValue, int targetValue) {
        return (fieldValue == null || fieldValue <= targetValue) ? true : false;
    }

    /**
     * Check if startDate <= endDate
     * Return true when check fails.
     * @param startDate      - Instant value of startDate
     * @param endDate        - Instant value of endDate
     * @param includeEqualTo - Can startDate = endDate
     */
    public static boolean checkIfEndDateGreaterThenStartDate(Instant startDate, Instant endDate,
            boolean includeEqualTo) {
        return ((startDate.isEqual(endDate) && !includeEqualTo) || endDate.isBefore(startDate));
    }
}
