package com.cannontech.web.api.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

/**
 * Helper class for controller (MVC) that called Rest Api. 
 *
 */
public class ApiControllerHelper {
    @Autowired GlobalSettingDao globalSettingDao;
    private static String webserverURL = "";
    
    /**
     * Populate binding error from the error object received from rest call. 
     */
    public void populateBindingError(BindingResult result, BindException error, ResponseEntity<? extends Object> errorResponse) {
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
     * Fetch the webserver URL
     */
    public static String getWebserverURL(HttpServletRequest request) {
        if (webserverURL.equals("")) {
            String serverPort = Integer.toString(request.getLocalPort());
            setWebserverURL("http://127.0.0.1:" + serverPort);
        }
        return webserverURL;
    }

    /**
     * Set the webserver URL
     */
    public static void setWebserverURL(String webserverURL) {
        ApiControllerHelper.webserverURL = webserverURL;
    }

    /**
     * Returns the Yukon Internal Url.
     */
    public String getYukonInternalUrl() {
        return globalSettingDao.getString(GlobalSettingType.YUKON_INTERNAL_URL);
    }

    /**
     * Generate dynamic URL for API calls
     */
    public String getApiURL(String prefixURL, String suffixURL) {
        return prefixURL + "/api" + suffixURL;
    }
}
