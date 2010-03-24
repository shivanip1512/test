package com.cannontech.common.validator;

import java.util.List;

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
}
