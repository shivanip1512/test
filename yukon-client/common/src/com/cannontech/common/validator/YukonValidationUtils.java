package com.cannontech.common.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
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
    public static final String BASIC_URL_PATH_REGEX = "\\A"+ BASIC_URL_PATH_FRAGMENT +"\\Z";
    public static final String BASIC_RESTFUL_URL_REGEX = "\\Ahttps?\\://([a-zA-Z0-9_\\-]+\\.)*[a-zA-Z0-9]+(\\:[0-9]+)?"+ BASIC_URL_PATH_FRAGMENT +"\\Z";

    public static boolean isBasicUrl(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(BASIC_RESTFUL_URL_REGEX);
    }

    public static boolean isUrlPath(String input) {
        if (input == null) {
            return false;
        }
        return input.matches(BASIC_URL_PATH_REGEX);
    }

	public static void checkExceedsMaxLength(Errors errors, String field, String fieldValue, int max) {
		
		if (fieldValue != null && fieldValue.length() > max) {
			errors.rejectValue(field, "yukon.web.error.exceedsMaximumLength", new Object[]{max}, "Exceeds maximum length of " + max);
		}
	}

   public static void checkIsBlank(Errors errors, String field, String fieldValue) {
        
        if (fieldValue != null && StringUtils.isBlank(fieldValue)) {
            errors.rejectValue(field, "yukon.web.error.isBlank", "Cannot be blank.");
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

    public static List<MessageSourceResolvable> errorsForBindingResult(
            BindingResult bindingResult) {
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
                new YukonMessageSourceResolvable(objectError.getCodes(),
                                                 objectError.getArguments(),
                                                 objectError.getDefaultMessage());
            retVal.add(message);
        }
        
        if(includeFieldErrors){
            //field errors
            Iterable<ObjectError> fieldErrors = Iterables.filter(bindingResult.getFieldErrors(), ObjectError.class);
            for (ObjectError objectError : fieldErrors) {
                YukonMessageSourceResolvable message =
                    new YukonMessageSourceResolvable(objectError.getCodes(),
                                                     objectError.getArguments(),
                                                     objectError.getDefaultMessage());
                retVal.add(message);
            }
        }
        
        if(!includeFieldErrors) {
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
     *  both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, String... fields) {
        rejectValues(errors, errorMessageKey, null, fields);
    }
    
    /**
     * This method allows you to use one error key for multiple fields. 
     * A good example of this would be a date range.  If the startDate is after the stopDate
     *  both fields should be flagged as having an error.
     */
    public static void rejectValues(Errors errors, String errorMessageKey, Object[] errorArgs, String... fields) {
        for (int i = 0; i < fields.length-1; i++) {
            String fieldName = fields[i];
            errors.rejectValue(fieldName, "yukon.web.defaults.blank");
        } 
        errors.rejectValue(fields[fields.length-1], errorMessageKey, errorArgs, "yukon.web.defaults.blank");
    }
}