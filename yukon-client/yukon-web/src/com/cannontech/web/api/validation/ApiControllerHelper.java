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
     *  Generate dynamic URL for API calls
     */
    public String getApiURL(HttpServletRequest request, String pathURL) {

        String url = request.getRequestURL().toString();
        String baseURL = url.substring(0, url.length() - request.getRequestURI().length());
        String apiURL;
        String contextPath = request.getContextPath();
        if (contextPath.isBlank()) {
            apiURL = baseURL + "/api" + request.getServletPath() + pathURL;
        } else {
            apiURL = baseURL + contextPath + "/api" + request.getServletPath() + pathURL;
        }
        apiURL = apiURL.replaceAll("(?<!\\:)/+", "/");
        return apiURL;
    }

}
