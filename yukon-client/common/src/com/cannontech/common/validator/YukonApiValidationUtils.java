package com.cannontech.common.validator;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.stars.util.ServletUtils;

public class YukonApiValidationUtils extends ValidationUtils {
    @Autowired private static PaoDao paoDao;

    public YukonApiValidationUtils(PaoDao paoDao) {
        YukonApiValidationUtils.paoDao = paoDao;
    }

    /**
     * Validate Pao name.
     */
    public static void validateName(String paoName, Errors errors, String fieldName) {
        checkIfFieldRequired("name", errors, paoName, fieldName);
        if (!errors.hasFieldErrors("name")) {
            checkExceedsMaxLength(errors, "name", paoName, 60);
            if (!PaoUtils.isValidPaoName(paoName)) {
                errors.rejectValue("name", ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            }
        }
    }

    /**
     * Checks whether the Pao name is unique or not
     */

    public static void validateNewPaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        validateName(paoName, errors, fieldName);
        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable("id");
            // Check if pao name already exists
            if (type != null && (paoId == null || !(paoDao.getYukonPAOName(Integer.valueOf(paoId)).equalsIgnoreCase(paoName)))) {
                validateUniquePaoName(paoName, type, errors, fieldName);
            }
        }
    }
    
   /**
    * Checks whether the Pao name is unique or not
    */
   private static void validateUniquePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
       LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
       if (unique != null) {
           errors.rejectValue("name", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { fieldName }, "");
       }
   }
    public static boolean checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
        if (YukonValidationUtilsCommon.checkExceedsMaxLength(fieldValue, max)) {
            errors.rejectValue(field, ApiErrorDetails.MAX_LENGTH_EXCEEDED.getCodeString(), new Object[] { max },
                    "Exceeds maximum length of " + max);
            return true;
        }
        return false;
    }

    /**
     * Return true if the provided fieldValue contains any characters from blacklisted characters( \\, !, #, $, %, &, ', *, (, ), ;,
     * +, =, <, >, ?, {, }, \, ", |, / and , ).
     */
    public static boolean checkBlacklistedCharacter(Errors errors, String field, String fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkBlacklistedCharacter(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            return true;
        }
        return false;
    }
    
    /**
     * Return true if the provided fieldValue contains only characters from whitelisted characters( A-Z, a-z, 0-9, $, _ and . ).
     */
    public static boolean checkWhitelistedCharacter(Errors errors, String field, String fieldValue, String fieldName) {
        if (!YukonValidationUtilsCommon.checkWhitelistedCharacter(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.WHITELIST_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            return false;
        }
        return true;
    }

    /**
     * Return true if the provided fieldValue contains any characters from illegal characters( \, |, /, ", \\ and , ).
     */
    public static boolean checkIllegalCharacter(Errors errors, String field, String fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIllegalCharacter(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { fieldName }, "");
            return true;
        }
        return false;
    }

    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, boolean fieldAllowsNull,
            String fieldName) {
        if (YukonValidationUtilsCommon.checkIsBlank(fieldValue, fieldAllowsNull)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
            return true;
        }
        return false;
    }

    /*
     * Convenience method to combine the above three common operations i.e. checkExceedsMaxLength(), checkBlacklistedCharacter(),
     * checkIsBlank().
     */
    public static void checkIsBlankOrExceedsMaxLengthOrBlacklistedChars(Errors errors, String field, String fieldValue,
            boolean fieldAllowsNull, int max, String fieldName) {
        checkIsBlank(errors, field, fieldValue, fieldAllowsNull, fieldName);
        checkExceedsMaxLength(errors, field, fieldValue, max);
        checkBlacklistedCharacter(errors, field, fieldValue, fieldName);
    }

    public static void checkIsPositiveShort(Errors errors, String field, Short fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveShort(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                    "must be a positive whole number.");
        }
    }

    public static void checkIsPositiveInt(Errors errors, String field, Integer fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveInt(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                    "must be a positive whole number.");
        }
    }

    public static void checkIsPositiveDouble(Errors errors, String field, Double fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveDouble(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "" },
                    "must be a positive number.");
        }
    }

    public static boolean checkIsValidDouble(Errors errors, String field, Double fieldValue) {
        if (!YukonValidationUtilsCommon.checkIsValidDouble(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.INVALID_VALUE.getCodeString(),
                    new Object[] { Double.MIN_VALUE + " - " + Double.MAX_VALUE }, "");
            return false;
        }
        return true;
    }

    public static void checkIsValidNumber(Errors errors, String field, Number fieldValue) {
        if (fieldValue == null) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { field }, "");
        } else if (fieldValue instanceof Double && !YukonValidationUtilsCommon.checkIsNumberPositiveDouble(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "" },
                    "must be a positive number.");
        } else if (fieldValue instanceof Integer && !YukonValidationUtilsCommon.checkIsNumberPositiveInt(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                    "must be a positive whole number.");
        }
    }

    /**
     * Check to ensure that the given value is between the given min and max value. While this will
     * work with
     * any type that implements {@link Comparable}, it should not be used with anything that
     * requires formatting
     * before being displayed to the user (like dates).
     */
    public static <T extends Comparable<T>> void checkRange(Errors errors, String field, T fieldValue, T min, T max,
            boolean required) {
        if (fieldValue == null) {
            if (required) {
                errors.rejectValue(field, Integer.toString(ApiErrorDetails.FIELD_REQUIRED.getCode()), new Object[] { field },
                        "");
            }
            return;
        }

        if (fieldValue.compareTo(min) < 0 || fieldValue.compareTo(max) > 0) {
            errors.rejectValue(field, ApiErrorDetails.VALUE_OUTSIDE_VALID_RANGE.getCodeString(), new Object[] { "", min, max },
                    "Must be between " + min
                            + " and " + max + ".");
        }
    }

    /**
     * Check to ensure that the given value is between the given range, expects a fully inclusive Range.
     */
    public static <T extends Comparable<T>> void checkRange(Errors errors, String field, String fieldname, T fieldValue,
            Range<T> range, boolean required) {
        if (fieldValue == null && !required) {
            return;
        }

        if (YukonValidationUtilsCommon.checkRange(fieldValue, range)) {
            errors.rejectValue(field, ApiErrorDetails.VALUE_OUTSIDE_VALID_RANGE.getCodeString(),
                    new Object[] { fieldname, range.getMin(),
                            range.getMax() },
                    "");
        }
    }

    public static void ipHostNameValidator(Errors errors, String field, String fieldValue) {
        checkIsBlank(errors, field, fieldValue, false, "IP Address");
        if (YukonValidationUtilsCommon.ipHostNameValidator(errors, field, fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "valid IP/Host Name" },
                    "");
        }
    }

    public static void validatePort(Errors errors, String field, String fieldName, String fieldValue) {
        if (!StringUtils.isBlank(fieldValue)) {
            if (!errors.hasFieldErrors(field)) {
                Range<Integer> range = Range.inclusive(1, 65535);
                try {
                    Integer portID = Integer.valueOf(fieldValue);
                    checkRange(errors, field, fieldName, Integer.valueOf(portID), range, true);
                } catch (Exception e) {
                    errors.rejectValue(field, ApiErrorDetails.VALUE_OUTSIDE_VALID_RANGE.getCodeString(),
                            new Object[] { fieldName, range.getMin(),
                                    range.getMax() },
                            "");
                }
            }
        }
    }

    /* Validate string for exact length. */
    public static void checkExactLength(String field, Errors errors, String fieldValue, String fieldName,
            int stringLength) {
        if (!YukonValidationUtilsCommon.checkExactLength(fieldValue, stringLength)) {
            errors.rejectValue(field, ApiErrorDetails.INVALID_FIELD_LENGTH.getCodeString(), new Object[] { fieldName , stringLength, fieldValue }, "");
        }
    }

    /* Validate a required list is empty */
    public static void checkIfListRequired(String field, Errors errors, List<?> fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfListRequired(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    /* Validate field is required */
    public static void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    /**
     * FieldValue must be not empty.
     * 
     * @param field           - model object name
     * @param fieldValue      - value of field
     * @param messageArg      - field name text for error message
     * @param fieldAllowsNull
     */
    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, String messageArg,
            boolean fieldAllowsNull) {
        if (YukonValidationUtilsCommon.checkIsBlank(fieldValue, fieldAllowsNull)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { messageArg }, "");
            return true;
        }
        return false;
    }

    /**
     * Check if fieldValue <= targetValue
     * 
     * @param field           - model object name
     * @param fieldValue      - value of field
     * @param targetValue     - value you are testing if fieldValue is greater then
     * @param fieldAllowsNull
     */
    public static void checkIsFieldValueGreaterThenTargetValueInt(String field, Integer fieldValue, int targetValue,
            Errors errors) {
        if (YukonValidationUtilsCommon.checkIsFieldValueGreaterThenTargetValueInt(fieldValue, targetValue)) {
            errors.rejectValue(field, ApiErrorDetails.VALUE_BELOW_MINIMUM.getCodeString(), new Object[] { targetValue }, "");
        }
    }

    /**
     * Check if startDate <= endDate
     * 
     * @param startField     - model object name you want to display error for
     * @param startDate      - Instant value of startDate
     * @param endDate        - Instant value of endDate
     * @param includeEqualTo - Can startDate = endDate
     */
    public static void checkIfEndDateGreaterThenStartDate(String startField, Instant startDate, Instant endDate,
            boolean includeEqualTo, Errors errors) {
        if (YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(startDate, endDate, includeEqualTo)) {
            if (includeEqualTo) {
                errors.rejectValue(startField, ApiErrorDetails.START_DATE_BEFORE_END_DATE.getCodeString(), new Object[] { "" },
                        "");
            }
            errors.rejectValue(startField, ApiErrorDetails.START_DATE_BEFORE_END_DATE.getCodeString(),
                    new Object[] { "or equal to" }, "");
        }
    }

}
