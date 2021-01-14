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
   
    public static  boolean checkExceedsMaxLength(String fieldValue, int max) {
        return (fieldValue != null && fieldValue.length() > max) ? true : false;
    }
    
    public static boolean checkBlacklistedCharacter(String fieldValue) {
        if (fieldValue != null) {
            Matcher hasBlacklistedChar = Pattern.compile(YukonValidationUtils.BASIC_BLACKLISTED_CHAR_LIST).matcher(fieldValue);
            return (hasBlacklistedChar.find()) ? true : false ;
        }
        return false;
    }

    /**
     * @deprecated - Use {LINK #checkIsBlank(String fieldValue, boolean fieldAllowsNull)}
     */
    // @Deprecated(since="7.5", forRemoval=true)
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

    public static boolean checkIsValidNumberDouble(Number fieldValue) {
        if (fieldValue instanceof Double) {
            if(!checkIsValidDouble(fieldValue.doubleValue()) && fieldValue.doubleValue() < 0) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean checkIsValidNumberInt(Number fieldValue) {
        if (fieldValue instanceof Integer) {
            if(fieldValue.intValue() < 0) {
                return false;
            }
        }
        return true;
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
        return (fieldValue == null) ? true : false;
    }

    /**
     * FieldValue must be not empty.
     * 
     * @param field           - model object name
     * @param fieldValue      - value of field
     * @param messageArg      - field name text for error message
     * @param fieldAllowsNull
    
    public static boolean checkIsBlank(String fieldValue, boolean fieldAllowsNull) {
        return (!(fieldAllowsNull && fieldValue == null) && StringUtils.isBlank(fieldValue)) ? true : false;
          //  errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { messageArg }, "");
    } */

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
     * 
     * @param startDate      - Instant value of startDate
     * @param endDate        - Instant value of endDate
     * @param includeEqualTo - Can startDate = endDate
     */
    public static boolean checkIfEndDateGreaterThenStartDate(Instant startDate, Instant endDate,
            boolean includeEqualTo) {
        if ((startDate.isEqual(endDate) && !includeEqualTo) || endDate.isBefore(startDate)) {
            return (includeEqualTo) ? true : false;
        }
        return false;
    }
}
