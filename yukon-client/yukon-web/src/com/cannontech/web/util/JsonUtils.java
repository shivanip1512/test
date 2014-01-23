package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.google.common.collect.Maps;

/**
 * EXAMPLE USAGE @ CONTROLLER
 *   @ RequestBody Map<String, Object> myMethod(..., YukonUserContext context) {
            MessageSourceAccessor messenger = resolver.getMessageSourceAccessor(context);
 *   	...
 * 		if (mySpecialCase) {
 * 			return JsonUtils.getErrorJson("password", "this.is.a.message.key", accessor);
 * 		}
 * 		return JsonUtils.getSuccessJson("success.message.key", accessor)
 *  }
 * 
 * After calling this on the server you can do something like this in your Javascript:
 *      // NOTE: This doesn't do the full Spring error reporting - it just displays the errors in a single list.
        }).done(function(results) {
            form.find('input').removeClass("error"); // remove all previous errors
            if (results.success) {
                jQuery("#dlg_change_password").dialog('close'); // This is run from a dialog, so close it if successful
            } else {
                var err_string = '<ol class="errors">'; // start an errors list
                for(var ii=0; ii < results.errors.length; ii++) { // Go through each error returned
                    var err=results.errors[ii];
                    err_string += '<li><i class="icon icon-cross"></i>'+ err.message +'</li>'; // get the message
                    form.find('input[name="'+ err.field +'"]').addClass("error"); // make the field's input red
                }
                var err_list = jQuery(".password_errors");
                err_list.html(err_string +'</ol>'); // set the list
                err_list.show();
            }
            form.find("input").removeAttr('disabled'); // re-enable the field after disabling it for AJAX processing.
        });
 * 
 * 
 * NOTE: We're using json.success [true|false] as the primary flag notifying the client that all succeeded or something failed.
 *       This is because the other philosophy of using HTTP statuses doesn't allow us to return data by AJAX.  Example:
 *       @ Server:
 *         response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // typical blankd failure code
 *       @ JQuery to process that:
 *         .fail(function(code, message, jqXHR) {....
 *       versus JQuery to process normal AJAX:
 *         .done(function(data, ...) {...
 */
public class JsonUtils {

    /**
     * Sets json.success = true, and json.message = {your message}
     */
    public static Map<String, Object> getSuccessJson(MessageSourceResolvable messageObj, MessageSourceAccessor accessor) {
        String message = accessor.getMessage(messageObj);
        Map<String, Object> json = new HashMap<>();
        json.put("success", true);
        json.put("message", message);
        return json;
    }

    /**
     * Call this when a JSON-based action needs to fail and returns the list of errors as Map<String, Object>s.
     * @param errors        Errors or BindingResult
     * @param accessor      MessageSourceAccessor to interpret message keys.
     * @return              result a Map<String, Object>
     * 
     * @postcondition       result['success'] = false
     * @postcondition       result['errors'] = List<Object>[ 0+ Map<String, Object>[field:{String}, message:{String}, severity:"ERROR"]]
     */
    public static Map<String, Object> getErrorJson(Errors errors, MessageSourceAccessor accessor) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("success", false);
        List<Object> errorList = new ArrayList<>();

        for (ObjectError err : errors.getGlobalErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", "GLOBAL");
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        for (FieldError err : errors.getFieldErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            Map<String, Object> errorJson = Maps.newHashMapWithExpectedSize(3);
            errorJson.put("field", err.getField());
            errorJson.put("message", msg);
            errorJson.put("severity", "ERROR");
            errorList.add(errorJson);
        }
        result.put("errors", errorList);

        return result;
    }

}
