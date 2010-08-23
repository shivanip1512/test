package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Lists;

public class YukonValidationUtils extends ValidationUtils {

	public static void checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
		
		if (fieldValue != null && fieldValue.length() > max) {
			errors.rejectValue(field, "yukon.web.error.exceedsMaximumLength", new Object[]{max}, "Exceeds maximum length of " + max);
		}
	}

    public static <T extends Comparable<T>> void checkRange(Errors errors,
            String field, T fieldValue, T min, T max, boolean required) {
        if (fieldValue == null) {
            if (required) {
                errors.rejectValue(field, "yukon.web.error.required",
                                   "Field is required");
            }
            return;
        }

        if (fieldValue.compareTo(min) < 0 || fieldValue.compareTo(max) > 0) {
            errors.rejectValue(field, "yukon.web.error.outOfRange",
                               new Object[] { min, max },
                               "Must be between " + min + " and " + max + ".");
        }
    }

    public static void regexCheck(Errors errors, String field,
            String fieldValue, Pattern pattern, String errorCode) {
        Matcher matcher = pattern.matcher(fieldValue);
        if (!matcher.matches()) {
            errors.rejectValue(field, errorCode);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<MessageSourceResolvable> errorsForBindingResult(
            BindingResult bindingResult) {
        List<MessageSourceResolvable> retVal = Lists.newArrayList();

        // global
        List<ObjectError> globalErrors =
            (List<ObjectError>) bindingResult.getGlobalErrors();
        for (ObjectError objectError : globalErrors) {
            YukonMessageSourceResolvable message =
                new YukonMessageSourceResolvable(objectError.getCodes(),
                                                 objectError.getArguments(),
                                                 objectError.getDefaultMessage());
            retVal.add(message);
        }

        int numErrors = bindingResult.getFieldErrorCount();
        if (numErrors == 1) {
            retVal.add(new YukonMessageSourceResolvable("yukon.web.error.fieldErrorExists"));
        } else if (numErrors > 1) {
            retVal.add(new YukonMessageSourceResolvable("yukon.web.error.fieldErrorsExist"));
        }

        return retVal;
	}

    /**
     * This method allows you to use one error key for multiple fields. 
     * A good example of this would be a date range.  If the startDate is after the stopDate
     *  both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, String... fields) {
        for (int i = 0; i < fields.length-1; i++) {
            String fieldName = fields[i];
            errors.rejectValue(fieldName, null, "");
        } 
        errors.rejectValue(fields[fields.length-1], errorMessageKey);
    }
}
