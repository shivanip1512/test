package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.validation.Errors;
import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.util.Range;

public class YukonApiValidationUtils {

    public static boolean checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
        if (fieldValue != null && fieldValue.length() > max) {
            errors.rejectValue(field, ApiErrorDetails.MAX_LENGTH_EXCEEDED.getCodeString(), new Object[] { max },
                    "Exceeds maximum length of " + max);
            return true;
        }
        return false;
    }

    public static boolean checkBlacklistedCharacter(Errors errors, String field, String fieldValue) {

        if (fieldValue != null) {
            Matcher hasBlacklistedChar = Pattern.compile(YukonValidationUtils.BASIC_BLACKLISTED_CHAR_LIST).matcher(fieldValue);
            if (hasBlacklistedChar.find()) {
                errors.rejectValue(field, ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString());
                return true;
            }
        }
        return false;
    }

    /**
     * @deprecated - Use {LINK #checkIsBlank(Errors errors, String field, String fieldValue, String messageArg, boolean
     *             fieldAllowsNull)}
     */
    // @Deprecated(since="7.5", forRemoval=true)
    public static boolean checkIsBlank(Errors errors, String field, String fieldValue, boolean fieldAllowsNull) {
        // Skips error message when the field allows null and the field value is null,
        // otherwise validates using isBlank.
        if (!(fieldAllowsNull && fieldValue == null) && StringUtils.isBlank(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldValue }, "");
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
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                    "must be a positive whole number.");
        }
    }

    public static void checkIsPositiveInt(Errors errors, String field, Integer fieldValue) {
        if (fieldValue == null || fieldValue < 0) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                    "must be a positive whole number.");
        }
    }

    public static void checkIsPositiveDouble(Errors errors, String field, Double fieldValue) {
        if (checkIsValidDouble(errors, field, fieldValue) && fieldValue < 0) {
            errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "" },
                    "must be a positive number.");
        }
    }

    public static boolean checkIsValidDouble(Errors errors, String field, Double fieldValue) {
        if (fieldValue == null || Double.isNaN(fieldValue) || Double.isInfinite(fieldValue)) {
            errors.rejectValue(field, ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { fieldValue }, "");
            return false;
        }
        return true;
    }

    public static void checkIsValidNumber(Errors errors, String field, Number fieldValue) {
        if (fieldValue == null) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldValue }, "");
        } else if (fieldValue instanceof Double) {
            if (checkIsValidDouble(errors, field, fieldValue.doubleValue()) && fieldValue.doubleValue() < 0) {
                errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "" },
                        "must be a positive number.");
            }
        } else if (fieldValue instanceof Integer) {
            if (fieldValue.intValue() < 0) {
                errors.rejectValue(field, ApiErrorDetails.IS_NOT_POSITIVE.getCodeString(), new Object[] { "whole" },
                        "must be a positive whole number.");
            }
        }
    }

    /**
     * Check to ensure that the Data Archiving Interval is less than or equal to the Interval Data Gathering Duration
     */
    public static void checkIsDataArchivingIntervalTooLarge(Errors errors, String field, Integer dataArchivingInterval,
            Integer intervalDataGatheringDuration) {
        // intervalDataGatheringDuration is multiplied by 60 to convert minutes into seconds.
        if (dataArchivingInterval > (intervalDataGatheringDuration * 60)) {
            errors.rejectValue(field, ApiErrorDetails.DATA_ARCHIVING_INTERVEL_TOO_LARGE.getCodeString());
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
                errors.rejectValue(field, Integer.toString(ApiErrorDetails.FIELD_REQUIRED.getCode()), new Object[] { fieldValue },
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

        if (fieldValue == null || (fieldValue != null && !range.intersects(fieldValue))) {
            errors.rejectValue(field, ApiErrorDetails.VALUE_OUTSIDE_VALID_RANGE.getCodeString(),
                    new Object[] { fieldname, range.getMin(),
                            range.getMax() },
                    "");
        }
    }

    /**
     * This method allows you to use one error key for multiple fields.
     * A good example of this would be a date range. If the startDate is after the stopDate
     * both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, String... fields) {
        rejectValues(errors, errorMessageKey, null, fields);
    }

    /**
     * This method allows you to use one error key for multiple fields.
     * A good example of this would be a date range. If the startDate is after the stopDate
     * both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, Object[] errorArgs, String... fields) {
        for (int i = 0; i < fields.length - 1; i++) {
            String fieldName = fields[i];
            errors.rejectValue(fieldName, "yukon.common.blank"); // ApiErroDetail
        }
        errors.rejectValue(fields[fields.length - 1], errorMessageKey, errorArgs, "yukon.common.blank");
    }

    public static void ipHostNameValidator(Errors errors, String field, String fieldValue) {
        Pattern ipHostNameMatcher = Pattern.compile(
                "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");
        errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "IP Address" }, "IP Address");
        if (!errors.hasFieldErrors(field)) {
            if (!ipHostNameMatcher.matcher(fieldValue).matches()) {
                errors.rejectValue(field, ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "IP Address" },
                        "IP Address");
            }
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
        if (fieldValue != null && fieldValue.length() != stringLength) {
            errors.rejectValue(field, ApiErrorDetails.INVALID_STRING_LENGTH.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    /* Validate a required list is empty */
    public static void checkIfListRequired(String field, Errors errors, List<?> fieldValue, String fieldName) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            errors.rejectValue(field, ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { fieldName }, "");
        }
    }

    /* Validate field is required */
    public static void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (fieldValue == null) {
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
        if (!(fieldAllowsNull && fieldValue == null) && StringUtils.isBlank(fieldValue)) {
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
        if (fieldValue == null || fieldValue <= targetValue) {
            errors.rejectValue(field, ApiErrorDetails.NOT_GREATER_THAN_INT.getCodeString(), new Object[] { targetValue }, "");
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
        if ((startDate.isEqual(endDate) && !includeEqualTo) || endDate.isBefore(startDate)) {
            if (includeEqualTo) {
                errors.rejectValue(startField, ApiErrorDetails.START_DATE_BEFORE_END_DATE.getCodeString(), new Object[] { "" },
                        "");

            }
            errors.rejectValue(startField, ApiErrorDetails.START_DATE_BEFORE_END_DATE.getCodeString(),
                    new Object[] { "or equal to" }, "");
        }
    }

}
