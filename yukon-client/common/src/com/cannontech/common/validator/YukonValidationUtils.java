package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

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
    public static final String BASIC_BLACKLISTED_CHAR_LIST = "[\\\\!#$%&'*();+=<>?{}\"|,/]";

    public static boolean isUrlPath(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(BASIC_URL_PATH_REGEX);
    }
    
    /**
     * Check to ensure that the serial Number of an RFN device is a valid numeric value
     */
    public static boolean isRfnSerialNumberValid(String serialNumber) {
        
        if (StringUtils.isEmpty(serialNumber)) {
            return true;
        }
        
        if (serialNumber.length() <= 30) {
            return true;
        }
        return false;
    }

    public static boolean checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
        if (fieldValue != null && fieldValue.length() > max) {
            errors.rejectValue(field, "yukon.web.error.exceedsMaximumLength", new Object[] { max },
                "Exceeds maximum length of " + max);
            return true;
        }
        return false;
    }

    public static boolean checkBlacklistedCharacter(Errors errors, String field, String fieldValue) {
        Matcher hasBlacklistedChar = Pattern.compile(BASIC_BLACKLISTED_CHAR_LIST).matcher(fieldValue);
        if (fieldValue != null && hasBlacklistedChar.find()) {
            errors.rejectValue(field, "yukon.web.error.isBlacklistedCharacter");
            return true;
        }
        return false;
    }

    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, boolean fieldAllowsNull) {
        // Skips error message when the field allows null and the field value is null,
        // otherwise validates using isBlank.
        if (!(fieldAllowsNull && fieldValue == null) && StringUtils.isBlank(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isBlank", "Cannot be blank.");
            return true;
        }
        return false;
    }

    /*
     * Convenience method to combine the above two common operations.
     */
    public static void checkIsBlankOrExceedsMaxLength(Errors errors, String field, String fieldValue,
                                                      boolean fieldAllowsNull, int max) {
        checkIsBlank(errors, field, fieldValue, fieldAllowsNull);
        checkExceedsMaxLength(errors, field, fieldValue, max);
        checkBlacklistedCharacter(errors, field, fieldValue);
    }

    public static void checkIsPositiveShort(Errors errors, String field, Short fieldValue) {
        if (fieldValue == null || fieldValue < 0) {
            errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
        }
    }
    
    public static void checkIsPositiveInt(Errors errors, String field, Integer fieldValue) {
        if (fieldValue == null || fieldValue < 0) {
            errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
        }
    }

    public static void checkIsPositiveDouble(Errors errors, String field, Double fieldValue) {
        if (checkIsValidDouble(errors, field, fieldValue) && fieldValue < 0) {
            errors.rejectValue(field, "yukon.web.error.isNotPositive");
        }
    }

    public static boolean checkIsValidDouble(Errors errors, String field, Double fieldValue) {
        if (fieldValue == null || Double.isNaN(fieldValue) || Double.isInfinite(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.notValidNumber");
            return false;
        }
        return true;
    }

    public static void checkIsValidNumber(Errors errors, String field, Number fieldValue) {
        if (fieldValue == null) {
            errors.rejectValue(field, "yukon.web.error.isBlank");
        } else if (fieldValue instanceof Double) {
            if (checkIsValidDouble(errors, field, fieldValue.doubleValue()) && fieldValue.doubleValue() < 0) {
                errors.rejectValue(field, "yukon.web.error.isNotPositive");
            }
        } else if (fieldValue instanceof Integer) {
            if (fieldValue.intValue() < 0) {
                errors.rejectValue(field, "yukon.web.error.isNotPositiveInt");
            }
        }
    }
    
    /**
     * Check to ensure that the Data Archiving Interval is less than or equal to the Interval Data Gathering Duration
     */
    public static void checkIsDataArchivingIntervalTooLarge(Errors errors, String field, Integer dataArchivingInterval, Integer intervalDataGatheringDuration) {
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

        if (required && (fieldValue.compareTo(min) < 0 || fieldValue.compareTo(max) > 0)) {
            errors.rejectValue(field, "yukon.web.error.outOfRange", new Object[] { min, max }, "Must be between " + min
                + " and " + max + ".");
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
                YukonMessageSourceResolvable message =
                    new YukonMessageSourceResolvable(objectError.getCodes(), objectError.getArguments(),
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

    public static void ipHostNameValidator(Errors errors, String field, String fieldValue ){
        Pattern ipHostNameMatcher =
                Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");
        rejectIfEmptyOrWhitespace(errors, "ipAddress", "yukon.web.error.ipAddressRequired");       
        if (!errors.hasFieldErrors(field)) {
           if (!ipHostNameMatcher.matcher(fieldValue).matches()) {
               errors.rejectValue(field, "yukon.web.error.invalidIPHostName");
           }
       }
   }
    
    public static void validatePort(Errors errors, String field, String fieldValue) {
        rejectIfEmptyOrWhitespace(errors, "port", "yukon.web.error.invalidPort");
        if (!errors.hasFieldErrors(field)) {
            try {
                 Integer portID = Integer.valueOf(fieldValue);
                 checkRange(errors, field, portID, 0, 65535, true);
            } catch (Exception e) {
                errors.rejectValue(field, "yukon.web.error.invalidPort");
            }
        }
    }
}


  