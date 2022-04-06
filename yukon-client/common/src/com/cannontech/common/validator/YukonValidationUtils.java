package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.util.Range;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class YukonValidationUtils extends ValidationUtils {
    /**
     * Simplified URL definitions as they're meant to be used internally to Yukon.
     * The largest part left out is the parameter string, eg. "?param1=val&..."
     */
    public static final String BASIC_URL_PATH_FRAGMENT = "(/[a-zA-Z0-9_\\-]+)*/?(\\.[a-z]+)?";
    public static final String BASIC_URL_PATH_REGEX = "\\A" + BASIC_URL_PATH_FRAGMENT + "\\Z";
    public static final String BASIC_RESTFUL_URL_REGEX = "\\Ahttps?\\://([a-zA-Z0-9_\\-]+\\.)*[a-zA-Z0-9]+(\\:[0-9]+)?"
        + BASIC_URL_PATH_FRAGMENT + "\\Z";
    public final static String VALID_RFN_SENSOR_SERIAL_NUMBER = "^[a-zA-Z0-9-_]+";

    public static boolean isUrlPath(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(BASIC_URL_PATH_REGEX);
    }
    
    /**
     * Check to ensure that the Serial Number of a RFN device is a valid value that only contains characters from : A-Z, a-z,
         * 0-9, _ or -.
     **/
    public static boolean isRfnSerialNumberValid(String serialNumber) {
        
        if (StringUtils.isEmpty(serialNumber)) {
            return true;
        }
        
        if (serialNumber.length() <= 30) {
            return true;
        }

        if(serialNumber != null) {
            Matcher isSerialNumberValid = Pattern.compile(VALID_RFN_SENSOR_SERIAL_NUMBER).matcher(serialNumber);
            return isSerialNumberValid.matches();
        }

        return false;
    }

    public static boolean checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
        if (YukonValidationUtilsCommon.checkExceedsMaxLength(fieldValue, max)) {
            errors.rejectValue(field, "yukon.web.error.exceedsMaximumLength", new Object[] { max },
                    "Exceeds maximum length of " + max);
            return true;
        }
        return false;
    }

    /**
     * Return true if the provided fieldValue contains any characters from blacklisted characters( \\, !, #, $, %, &, ', *, (, ), ;,
     * +, =, <, >, ?, {, }, \, ", |, / and , ).
     */
    public static boolean checkBlacklistedCharacter(Errors errors, String field, String fieldValue) {
        if (YukonValidationUtilsCommon.checkBlacklistedCharacter(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isBlacklistedCharacter");
            return true;
        }
        return false;
    }

    /**
     * Return true if the provided fieldValue contains only characters from whitelisted characters( A-Z, a-z, 0-9, $, _ and . ).
     */
    public static boolean checkWhitelistedCharacter(Errors errors, String field, String fieldValue, String fieldName) {
        if (!YukonValidationUtilsCommon.checkWhitelistedCharacter(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isWhitelistedCharacter");
            return false;
        }
        return true;
    }

    /**
     * Return true if the provided fieldValue contains any characters from illegal characters( \, |, /, ", \\ and , ).
     */
    public static boolean checkIllegalCharacter(Errors errors, String field, String fieldValue) {
        if (YukonValidationUtilsCommon.checkIllegalCharacter(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isBlacklistedCharacter");
            return true;
        }
        return false;
    }

    /**
     * Return true if the provided String contains any characters from illegal XML characters ( " ' < > and & )
     */
    public static boolean checkIllegalXmlCharacter(Errors errors, String field, String fieldValue) {
        if (YukonValidationUtilsCommon.checkIllegalXmlCharacter(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isIllegalXmlCharacter");
            return true;
        }
        return false;
    }

    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, boolean fieldAllowsNull) {
        if (YukonValidationUtilsCommon.checkIsBlank(fieldValue, fieldAllowsNull)) {
            errors.rejectValue(field, "yukon.web.error.isBlank", "Cannot be blank.");
            return true;
        }
        return false;
    }

    /*
     * Convenience method to combine the above three common operations i.e. checkExceedsMaxLength(), checkBlacklistedCharacter(),
     * checkIsBlank().
     */
    public static void checkIsBlankOrExceedsMaxLengthOrBlacklistedChars(Errors errors, String field, String fieldValue,
            boolean fieldAllowsNull, int max) {
        checkIsBlank(errors, field, fieldValue, fieldAllowsNull);
        checkExceedsMaxLength(errors, field, fieldValue, max);
        checkBlacklistedCharacter(errors, field, fieldValue);
    }

    public static void checkIsPositiveShort(Errors errors, String field, Short fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveShort(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
        }
    }
    
    public static void checkIsPositiveInt(Errors errors, String field, Integer fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveInt(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
        }
    }

    public static void checkIsPositiveDouble(Errors errors, String field, Double fieldValue) {
        if (YukonValidationUtilsCommon.checkIsPositiveDouble(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isNotPositive");
        }
    }

    public static boolean checkIsValidDouble(Errors errors, String field, Double fieldValue) {
        if (!YukonValidationUtilsCommon.checkIsValidDouble(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.notValidNumber");
            return false;
        }
        return true;
    }

    public static void checkIsNumberPositiveIntOrDouble(Errors errors, String field, Number fieldValue) {
        if (fieldValue == null) {
            errors.rejectValue(field, "yukon.web.error.isBlank");
        } else if (fieldValue instanceof Double && !YukonValidationUtilsCommon.checkIsNumberPositiveDouble(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isNotPositive");
        } else if (fieldValue instanceof Integer && !YukonValidationUtilsCommon.checkIsNumberPositiveInt(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
        }
    }
    
    /**
     * Check to ensure that the Data Archiving Interval is less than or equal to the Interval Data Gathering Duration
     */
    public static void checkIsDataArchivingIntervalTooLarge(Errors errors, String field, Integer dataArchivingInterval,
            Integer intervalDataGatheringDuration) {
        // intervalDataGatheringDuration is multiplied by 60 to convert minutes into seconds.
        if (dataArchivingInterval > (intervalDataGatheringDuration * 60)) {
            errors.rejectValue(field, "yukon.web.error.dataArchivingIntervalTooLarge");
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
                errors.rejectValue(field, "yukon.web.error.required", "Field is required");
            }
            return;
        }

        if (YukonValidationUtilsCommon.checkRange(fieldValue, min, max)) {
            errors.rejectValue(field, "yukon.web.error.outOfRange", new Object[] { min, max }, "Must be between " + min
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
            errors.rejectValue(field, "yukon.web.error.outOfRangeObject", new Object[] { fieldname, range.getMin(),
                    range.getMax() }, "");
        }
    }

    public static void regexCheck(Errors errors, String field, String fieldValue, Pattern pattern, String errorCode) {
        Matcher matcher = pattern.matcher(fieldValue);
        if (!matcher.matches()) {
            errors.rejectValue(field, errorCode);
        }
    }

    public static List<MessageSourceResolvable> errorsForBindingResult(BindingResult bindingResult) {
        return errorsForBindingResult(bindingResult, false);
    }

    /**
     * Think twice before using this method.  Then get some coffee, talk to your neighbor, and
     * think about using it again.  This method was created out of necessity: YUK-10443.
     * Example:
     * Thermostat schedules do not have a space for binding field errors.  Even though the UI does a good job
     * of validating/preventing error states on the schedules there are cases where errors might have existed
     * pre-update and we need to display that to the user.
     *
     * This is really the only reasonable case for including the FieldErrors in in the top level.
     *
     * @param bindingResult
     * @param includeFieldErrors    if set to true, all field errors will be returned in a flat list with the
     *                              global errors.
     * @return
     */
    public static List<MessageSourceResolvable> errorsForBindingResult(BindingResult bindingResult,
            boolean includeFieldErrors) {
        List<MessageSourceResolvable> retVal = Lists.newArrayList();

        // global
        Iterable<ObjectError> globalErrors = Iterables.filter(bindingResult.getGlobalErrors(), ObjectError.class);
        for (ObjectError objectError : globalErrors) {
            YukonMessageSourceResolvable message =
                new YukonMessageSourceResolvable(objectError.getCodes(), objectError.getArguments(),
                    objectError.getDefaultMessage());
            retVal.add(message);
        }

        if (includeFieldErrors) {
            // field errors
            Iterable<ObjectError> fieldErrors = Iterables.filter(bindingResult.getFieldErrors(), ObjectError.class);
            for (ObjectError objectError : fieldErrors) {
                YukonMessageSourceResolvable message = new YukonMessageSourceResolvable(objectError.getCodes(),
                        objectError.getArguments(),
                        objectError.getDefaultMessage());
                retVal.add(message);
            }
        }

        if (!includeFieldErrors) {
            int numErrors = bindingResult.getFieldErrorCount();
            if (numErrors == 1) {
                retVal.add(new YukonMessageSourceResolvable("yukon.web.error.fieldErrorExists"));
            } else if (numErrors > 1) {
                retVal.add(new YukonMessageSourceResolvable("yukon.web.error.fieldErrorsExist"));
            }
        }

        return retVal;
    }

    /**
     * This method allows you to use one error key for multiple fields.
     * A good example of this would be a date range.  If the startDate is after the stopDate
     * both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, String... fields) {
        rejectValues(errors, errorMessageKey, null, fields);
    }

    /**
     * This method allows you to use one error key for multiple fields.
     * A good example of this would be a date range.  If the startDate is after the stopDate
     * both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, Object[] errorArgs, String... fields) {
        for (int i = 0; i < fields.length - 1; i++) {
            String fieldName = fields[i];
            errors.rejectValue(fieldName, "yukon.common.blank");
        }
        errors.rejectValue(fields[fields.length - 1], errorMessageKey, errorArgs, "yukon.common.blank");
    }

    /* Validation for checking the latitude */
    public static boolean isLatitudeInRange(Double latitude) {
        if (latitude != null && !Double.isNaN(latitude)) {
            if (latitude > 90 || latitude < -90) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /* Validation for checking the longitude */
    public static boolean isLongitudeInRange(Double longitude) {
        if (longitude != null && !Double.isNaN(longitude)) {
            if (longitude > 180 || longitude < -180) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static void ipHostNameValidator(Errors errors, String field, String fieldValue) {
        rejectIfEmptyOrWhitespace(errors, field, "yukon.web.error.invalidIPHostName");
        if (YukonValidationUtilsCommon.ipHostNameValidator(errors, field, fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.invalidIPHostName");
        }
    }

    public static void validatePort(Errors errors, String field, String fieldName, String fieldValue) {
        if (!errors.hasFieldErrors(field)) {
            var range = Range.inclusive(1, 65535);
            try {
                Integer portID = Integer.valueOf(fieldValue);
                checkRange(errors, field, fieldName, portID, range, true);
            } catch (Exception e) {
                errors.rejectValue(field, "yukon.web.error.outOfRangeObject", 
                        new Object[] { fieldName, range.getMin(), range.getMax() }, "");
            }
        }
    }
  
    /* Validate string for exact length. */
    public static void checkExactLength(String field, Errors errors, String fieldValue, String fieldName,
            int stringLength) {
        if (!YukonValidationUtilsCommon.checkExactLength(fieldValue, stringLength)) {
            errors.rejectValue(field, "yukon.web.error.invalidStringLength", new Object[] { fieldName, stringLength }, "");
        }
    }

    /* Validate a required list is empty */
    public static void checkIfListRequired(String field, Errors errors, List<?> fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfListRequired(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.fieldrequired", new Object[] { fieldName }, "");
        }
    }
    

    /* Validate field is required */
    public static void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (YukonValidationUtilsCommon.checkIfFieldRequired(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.fieldrequired", new Object[] { fieldName }, "");
        }
    }
    
    /**
     * FieldValue must be not empty.
     * @param field - model object name
     * @param fieldValue - value of field
     * @param messageArg - field name text for error message
     * @param fieldAllowsNull
     */
    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, String messageArg,
            boolean fieldAllowsNull) {
        if (YukonValidationUtilsCommon.checkIsBlank(fieldValue, fieldAllowsNull)) {
            errors.rejectValue(field, "yukon.web.error.fieldrequired", new Object[] { messageArg }, "");
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
            errors.rejectValue(field, "yukon.web.error.notGreaterThanInt", new Object[] { targetValue }, "");
        }
    }

    /**
     * Check if startDate <= endDate
     * 
     * @param startField        - model object name you want to display error for
     * @param startDate         - Instant value of startDate
     * @param endDate           - Instant value of endDate
     * @param includeEqualTo - Can startDate = endDate
     */
    public static void checkIfEndDateGreaterThenStartDate(String startField, Instant startDate, Instant endDate,
            boolean includeEqualTo, Errors errors) {

        if (YukonValidationUtilsCommon.checkIfEndDateGreaterThenStartDate(startDate, endDate, includeEqualTo)) {
            String errorMessage = "yukon.web.error.date.startDateBeforeEndDate";
            if (includeEqualTo) {
                errorMessage = "yukon.web.error.date.startDateBeforeOrEqualEndDate";
            }
            errors.rejectValue(startField, errorMessage, new Object[] { startDate, endDate }, "");
        }
    }
}


  