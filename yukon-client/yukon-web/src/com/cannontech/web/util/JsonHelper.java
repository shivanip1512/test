package com.cannontech.web.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.cannontech.common.i18n.MessageSourceAccessor;

/**
 * @Autowire this into your controller to go from BindingResults/Errors to a JSON structure your AJAX can use.
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
@Component
public class JsonHelper {

    /**
     * Return from your controller's action method.  Sets json.success = true.
     */
    public JSONObject succeed() {
        JSONObject json = new JSONObject();
        json.put("success", true);
        return json;
    }

    /**
     * Return from your controller's action method.  Sets json.success = true, and json.message = {your message}
     * 
     * @param message Plaintext message (not key!) to be returned to the browser.
     */
    public JSONObject succeed(String message) {
        JSONObject json = succeed();
        json.put("message", message);
        return json;
    }


    /**
     * Call this when a JSON-based action needs to fail and returns the list of errors as JSONObjects.
     * @param errors        Errors or BindingResult
     * @param accessor     MessageSourceAccessor to interpret message keys.
     * @return              result a JSONObject
     * 
     * @postcondition       result['success'] = false
     * @postcondition       result['errors'] = JSONArray[ 0+ JSONObject[field:{String}, message:{String}, severity:"ERROR"]]
     */
    public JSONObject failToJSON(Errors errors, MessageSourceAccessor accessor) {
        return failToJSON(new JSONObject(), errors, accessor);
    }

    public JSONObject failToJSON(JSONObject result, Errors errors, MessageSourceAccessor accessor) {

        result.put("success", false);
        JSONArray errorList = new JSONArray();

        // Two near-identical loops because the API returns different objects....
        for (ObjectError err:errors.getGlobalErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            JSONObject error = makeJSONError("GLOBAL", msg);
            errorList.add(error);
        }
        for (FieldError err:errors.getFieldErrors()) {
            final String msg = accessor.getMessage(err.getCode(), err.getArguments());
            JSONObject error = makeJSONError(err.getField(), msg);
            errorList.add(error);
        }
        result.put("errors", errorList);

        return result;
    }

    /**
     * Extremely simple way to fail returning a JSONObject (result) with one error message on it.
     * @param fieldName         Likely a simple name (eg. "firstName") used in JS to mark the input field.
     * @param key       String defined within i18n files.
     * @param accessor     MessageSourceAccessor to interpret message keys.
     * @return
     */
    public JSONObject failToJSON(String fieldName, String key, MessageSourceAccessor accessor) {

        final String errorMsgString = accessor.getMessage(key);

        JSONObject json = new JSONObject();
        json.put("success", false);
        JSONArray errorList = new JSONArray();
        errorList.add(makeJSONError(fieldName, errorMsgString));
        json.put("errors", errorList);
        return json;
    }

    /**
     * Create a standard JSONObject with error fields set.
     * @param fieldName
     * @param message
     * @return
     */
    public JSONObject makeJSONError(String fieldName, String message) {
        JSONObject json = new JSONObject();
        json.put("field", fieldName);
        json.put("message", message);
        json.put("severity", "ERROR");
        return json;
    }

}
