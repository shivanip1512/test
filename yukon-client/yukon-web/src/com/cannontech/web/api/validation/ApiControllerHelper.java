package com.cannontech.web.api.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

/**
 * Helper class for controller (MVC) that called Rest Api. 
 *
 */
public class ApiControllerHelper {

    // Populate binding error from the error object received from rest call.
    public void populateBindingError(BindingResult result, BindException error, ResponseEntity<Object> errorResponse) {
        LinkedHashMap<?, ?> errorObject = (LinkedHashMap<?, ?>) errorResponse.getBody();
        ArrayList<?> fieldError = (ArrayList<?>) errorObject.get("fieldErrors");
        ArrayList<?> globalError = (ArrayList<?>) errorObject.get("globalErrors");

        fieldError.stream().forEach(e -> {
            error.rejectValue(((LinkedHashMap<?, ?>) e).get("field").toString(), StringUtils.EMPTY,
                ((LinkedHashMap<?, ?>) e).get("code").toString());
        });

        globalError.stream().forEach(e -> {
            error.reject(((LinkedHashMap<?, ?>) e).get("code").toString());
        });
        result.addAllErrors(error);
    }

    /**
     * Generate dynamic URL for API calls
     */
    public String getApiURL(HttpServletRequest request, String pathURL) {
        StringBuffer baseURL = request.getRequestURL();
        String apiURL = baseURL.substring(0, baseURL.indexOf(request.getServletPath())) + "/api"
            + request.getServletPath() + pathURL;
        return apiURL;
    }

}
